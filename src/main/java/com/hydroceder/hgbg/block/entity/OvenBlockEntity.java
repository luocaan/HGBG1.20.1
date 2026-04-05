package com.hydroceder.hgbg.block.entity;

import com.hydroceder.hgbg.DebugManager;
import com.hydroceder.hgbg.block.ModBlockEntityTypes;
import com.hydroceder.hgbg.block.OvenBlock;
import com.hydroceder.hgbg.recipe.ModRecipeTypes;
import com.hydroceder.hgbg.recipe.oven.OvenRecipe;
import com.hydroceder.hgbg.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 烤箱方块实体类
 * 存储物品和烤制时间
 */
public class OvenBlockEntity extends BlockEntity implements Inventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private int cookTime = 0;
    private static final int COOK_TIME_TOTAL = 200; // 10秒 = 200 ticks
    
    public OvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.OVEN_BLOCK_ENTITY, pos, state);
    }
    
    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < 3) {
            return items.get(slot);
        }
        return ItemStack.EMPTY;
    }
    
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < 3) {
            items.set(slot, stack);
            markDirty();
        }
    }
    
    public void startCooking() {
        cookTime = COOK_TIME_TOTAL;
        markDirty();
        broadcastDebug("烤箱开始烤制，剩余时间: " + cookTime + " ticks");
        
        // 播放烤箱工作音效
        if (world != null && !world.isClient) {
            world.playSound(null, pos, ModSounds.OVEN_WORKING, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }
    
    public boolean isCooking() {
        return cookTime > 0;
    }
    
    public void dropItems() {
        if (world != null && !world.isClient) {
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, stack.copy());
                    world.spawnEntity(itemEntity);
                }
            }
            clear();
        }
    }
    
    private void ejectResult(ItemStack result) {
        if (world != null && !world.isClient) {
            ItemEntity itemEntity = new ItemEntity(
                world, 
                pos.getX() + 0.5, 
                pos.getY() + 1.0, 
                pos.getZ() + 0.5, 
                result
            );
            world.spawnEntity(itemEntity);
            broadcastDebug("烤箱弹出结果: " + result.getName().getString() + " x" + result.getCount());
        }
    }
    
    private void finishCooking() {
        if (world == null || world.isClient) return;
        
        // 检查是否有火药或TNT
        boolean hasExplosive = false;
        for (ItemStack stack : items) {
            if (stack.isOf(Items.GUNPOWDER) || stack.isOf(Items.TNT)) {
                hasExplosive = true;
                break;
            }
        }
        
        if (hasExplosive) {
            // 给附近玩家授予进度
            for (PlayerEntity player : world.getPlayers()) {
                if (player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < 64) {
                    if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
                        serverPlayer.getAdvancementTracker().grantCriterion(
                            world.getServer().getAdvancementLoader().get(new Identifier("hunger-begone", "bake_explosive")),
                            "dummy"
                        );
                    }
                }
            }
            
            // 发生爆炸：10f，无火焰，不破坏方块
            world.createExplosion(
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                10.0f,
                false,
                World.ExplosionSourceType.NONE
            );
            broadcastDebug("烤箱内发现火药或TNT，发生爆炸！");
            
            // 清空烤箱
            for (int i = 0; i < items.size(); i++) {
                items.set(i, ItemStack.EMPTY);
            }
            cookTime = 0;
            
            // 更新方块状态为空_开
            BlockState state = getCachedState();
            world.setBlockState(pos, state.with(OvenBlock.OPEN, true).with(OvenBlock.ITEM_COUNT, 0), 3);
            
            markDirty();
            return;
        }
        
        broadcastDebug("烤箱烤制完成！");
        
        // 播放叮声音效
        world.playSound(null, pos, ModSounds.BELL, SoundCategory.BLOCKS, 1.0f, 1.0f);
        
        // 收集所有非空物品
        List<ItemStack> inputItems = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                inputItems.add(stack);
                broadcastDebug("输入物品: " + stack.getName().getString() + " x" + stack.getCount());
            }
        }
        
        // 查找匹配的配方
        ItemStack result = findMatchingRecipe(inputItems);
        
        // 弹出结果
        ejectResult(result);
        
        // 清空烤箱
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        cookTime = 0;
        
        // 更新方块状态为空_开
        BlockState state = getCachedState();
        world.setBlockState(pos, state.with(OvenBlock.OPEN, true).with(OvenBlock.ITEM_COUNT, 0), 3);
        
        broadcastDebug("烤箱状态更新: 满_关 → 空_开");
        
        markDirty();
    }
    
    private ItemStack findMatchingRecipe(List<ItemStack> inputItems) {
        if (world == null) return new ItemStack(Items.CHARCOAL);
        
        // 创建一个简单的库存用于配方匹配
        SimpleInventory inventory = new SimpleInventory(inputItems.toArray(new ItemStack[0]));
        
        // 查找匹配的烤箱配方
        Optional<OvenRecipe> matchingRecipe = world.getRecipeManager()
            .getAllMatches(ModRecipeTypes.OVEN_RECIPE_TYPE, inventory, world)
            .stream()
            .findFirst();
        
        if (matchingRecipe.isPresent()) {
            broadcastDebug("找到匹配配方！");
            return matchingRecipe.get().getOutput().copy();
        }
        
        // 如果没有匹配的配方，返回木炭
        broadcastDebug("未找到匹配配方，返回木炭");
        return new ItemStack(Items.CHARCOAL, inputItems.size());
    }
    
    /**
     * 广播调试消息给附近启用了调试的玩家
     */
    private void broadcastDebug(String message) {
        if (world == null || world.isClient) return;
        
        // 获取附近玩家并发送调试消息
        for (PlayerEntity player : world.getPlayers()) {
            if (player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < 4) {
                if (DebugManager.isDebugEnabled(player)) {
                    DebugManager.sendDebugMessage(player, message);
                }
            }
        }
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, OvenBlockEntity blockEntity) {
        if (world.isClient) return;
        
        if (blockEntity.cookTime > 0) {
            blockEntity.cookTime--;
            
            // 每50tick报告一次剩余时间
            if (blockEntity.cookTime % 50 == 0 && blockEntity.cookTime > 0) {
                blockEntity.broadcastDebug("烤制中... 剩余: " + blockEntity.cookTime + " ticks");
            }
            
            if (blockEntity.cookTime == 0) {
                blockEntity.finishCooking();
            } else {
                blockEntity.markDirty();
            }
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
        nbt.putInt("CookTime", cookTime);
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
        cookTime = nbt.getInt("CookTime");
    }
    
    @Override
    public int size() {
        return items.size();
    }
    
    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot).copy();
    }
    
    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(items, slot, amount);
    }
    
    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(items, slot);
    }
    
    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }
    
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (world == null || world.getBlockEntity(pos) != this) {
            return false;
        }
        return player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
    }
}
