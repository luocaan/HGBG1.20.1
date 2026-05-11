package com.hydroceder.hgbg.block.entity;

import com.hydroceder.hgbg.block.ModBlockEntityTypes;
import com.hydroceder.hgbg.event.StoveEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 灶台方块实体类
 * 用于存储锅物品和烹饪材料
 */
public class StoveBlockEntity extends BlockEntity implements Inventory {
    private ItemStack pan = ItemStack.EMPTY;
    private List<ItemStack> materials = new ArrayList<>();
    private boolean isCooking = false;
    private int cookTime = 0;
    private int totalCookTime = 0;
    private int soundTick = 0;
    private boolean hasValidRecipe = true;
    private UUID cookingPlayerUuid = null;
    
    public StoveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.STOVE_BLOCK_ENTITY, pos, state);
    }
    
    public ItemStack getPan() {
        return pan.copy();
    }
    
    public void setPan(ItemStack pan) {
        this.pan = pan.copy();
        markDirty();
    }
    
    public List<ItemStack> getMaterials() {
        List<ItemStack> result = new ArrayList<>(materials.size());
        for (ItemStack stack : materials) {
            result.add(stack.copy());
        }
        return result;
    }
    
    public void addMaterial(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        
        for (ItemStack existingStack : materials) {
            if (ItemStack.areItemsEqual(existingStack, stack) && (existingStack.getNbt() == null ? stack.getNbt() == null : existingStack.getNbt().equals(stack.getNbt()))) {
                existingStack.increment(stack.getCount());
                markDirty();
                return;
            }
        }
        
        materials.add(stack.copy());
        markDirty();
    }
    
    public void clearMaterials() {
        materials.clear();
        markDirty();
    }
    
    public boolean isCooking() {
        return isCooking;
    }
    
    public void startCooking(int cookTime, boolean hasValidRecipe, UUID playerUuid) {
        this.isCooking = true;
        this.cookTime = 0;
        this.totalCookTime = cookTime;
        this.hasValidRecipe = hasValidRecipe;
        this.cookingPlayerUuid = playerUuid;
        markDirty();
    }
    
    public void stopCooking() {
        this.isCooking = false;
        this.cookTime = 0;
        this.totalCookTime = 0;
        this.soundTick = 0;
        this.cookingPlayerUuid = null;
        markDirty();
    }
    
    public int getCookTime() {
        return cookTime;
    }
    
    public int getTotalCookTime() {
        return totalCookTime;
    }
    
    public void dropItems() {
        if (world != null && !world.isClient) {
            if (!pan.isEmpty()) {
                net.minecraft.util.ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), pan);
            }

            for (ItemStack material : materials) {
                if (!material.isEmpty()) {
                    net.minecraft.util.ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), material);
                }
            }
        }
    }

    private void suckInItems(World world, BlockPos pos) {
        Box collectionBox = new Box(
            pos.getX(), pos.getY(), pos.getZ(),
            pos.getX() + 1.0, pos.getY() + 0.5, pos.getZ() + 1.0
        );

        java.util.List<ItemEntity> itemEntities = world.getEntitiesByClass(ItemEntity.class, collectionBox, entity -> !entity.isRemoved() && !entity.getStack().isEmpty());

        for (ItemEntity itemEntity : itemEntities) {
            ItemStack itemStack = itemEntity.getStack();

            ItemStack toAdd = itemStack.copy();
            ActionResult eventResult = StoveEvents.PLACE_ITEM.invoker().onPlaceItem(world, pos, null, toAdd);
            if (eventResult == ActionResult.FAIL) {
                continue;
            }

            addMaterial(toAdd);

            itemStack.decrement(toAdd.getCount());
            if (itemStack.isEmpty()) {
                itemEntity.discard();
            } else {
                itemEntity.setStack(itemStack);
            }

            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.3f, 1.2f);
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!pan.isEmpty()) {
            nbt.put("Pan", pan.writeNbt(new NbtCompound()));
        }
        
        NbtList materialsList = new NbtList();
        for (ItemStack stack : materials) {
            if (!stack.isEmpty()) {
                NbtCompound stackNbt = new NbtCompound();
                stack.writeNbt(stackNbt);
                materialsList.add(stackNbt);
            }
        }
        nbt.put("Materials", materialsList);
        
        nbt.putBoolean("IsCooking", isCooking);
        nbt.putInt("CookTime", cookTime);
        nbt.putInt("TotalCookTime", totalCookTime);
        nbt.putBoolean("HasValidRecipe", hasValidRecipe);
        if (cookingPlayerUuid != null) {
            nbt.putUuid("CookingPlayerUuid", cookingPlayerUuid);
        }
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Pan")) {
            pan = ItemStack.fromNbt(nbt.getCompound("Pan"));
        } else {
            pan = ItemStack.EMPTY;
        }
        
        materials.clear();
        if (nbt.contains("Materials")) {
            NbtList materialsList = nbt.getList("Materials", 10);
            for (int i = 0; i < materialsList.size(); i++) {
                NbtCompound stackNbt = materialsList.getCompound(i);
                ItemStack stack = ItemStack.fromNbt(stackNbt);
                if (!stack.isEmpty()) {
                    materials.add(stack);
                }
            }
        }
        
        isCooking = nbt.getBoolean("IsCooking");
        cookTime = nbt.getInt("CookTime");
        totalCookTime = nbt.getInt("TotalCookTime");
        hasValidRecipe = nbt.getBoolean("HasValidRecipe");
        if (nbt.contains("CookingPlayerUuid")) {
            cookingPlayerUuid = nbt.getUuid("CookingPlayerUuid");
        }
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, StoveBlockEntity blockEntity) {
        if (!world.isClient) {
            if (state.get(com.hydroceder.hgbg.block.StoveBlock.HAS_PAN) && blockEntity.pan.isEmpty()) {
                world.setBlockState(pos, state.with(com.hydroceder.hgbg.block.StoveBlock.HAS_PAN, false), 3);
            } else if (!state.get(com.hydroceder.hgbg.block.StoveBlock.HAS_PAN) && !blockEntity.pan.isEmpty()) {
                world.setBlockState(pos, state.with(com.hydroceder.hgbg.block.StoveBlock.HAS_PAN, true), 3);
            }
            
            if (state.get(com.hydroceder.hgbg.block.StoveBlock.HAS_PAN) && !blockEntity.isCooking()) {
                blockEntity.suckInItems(world, pos);
            }
            
            if (blockEntity.isCooking) {
                blockEntity.cookTime++;
                blockEntity.soundTick++;
                
                if (blockEntity.soundTick % 20 == 0) {
                    world.playSound(null, pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.2f, 1.0f);
                }
                
                int remainingSeconds = (blockEntity.totalCookTime - blockEntity.cookTime + 19) / 20;
                
                if (blockEntity.cookingPlayerUuid != null) {
                    PlayerEntity cookingPlayer = world.getPlayerByUuid(blockEntity.cookingPlayerUuid);
                    if (cookingPlayer != null) {
                        Text message;
                        if (blockEntity.hasValidRecipe) {
                            message = Text.translatable("stove.cooking", remainingSeconds);
                        } else {
                            message = Text.translatable("stove.cooking_invalid", remainingSeconds);
                        }
                        cookingPlayer.sendMessage(message, true);
                    }
                }
                
                if (blockEntity.cookTime >= blockEntity.totalCookTime) {
                    world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    
                    blockEntity.stopCooking();
                    com.hydroceder.hgbg.block.StoveBlock.finishCooking(world, pos, state, blockEntity);
                }
                blockEntity.markDirty();
            }
        }
    }
    
    @Override
    public int size() {
        return 1 + materials.size();
    }
    
    @Override
    public boolean isEmpty() {
        return pan.isEmpty() && materials.isEmpty();
    }
    
    @Override
    public ItemStack getStack(int slot) {
        if (slot == 0) {
            return pan;
        } else if (slot > 0 && slot - 1 < materials.size()) {
            return materials.get(slot - 1);
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot == 0) {
            if (pan.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack result = pan.copy();
            result.setCount(Math.min(amount, pan.getCount()));
            pan.decrement(amount);
            markDirty();
            return result;
        } else if (slot > 0 && slot - 1 < materials.size()) {
            ItemStack material = materials.get(slot - 1);
            if (material.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack result = material.copy();
            result.setCount(Math.min(amount, material.getCount()));
            material.decrement(amount);
            if (material.isEmpty()) {
                materials.remove(slot - 1);
            }
            markDirty();
            return result;
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public ItemStack removeStack(int slot) {
        if (slot == 0) {
            ItemStack result = pan.copy();
            pan = ItemStack.EMPTY;
            markDirty();
            return result;
        } else if (slot > 0 && slot - 1 < materials.size()) {
            ItemStack result = materials.remove(slot - 1).copy();
            markDirty();
            return result;
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == 0) {
            pan = stack;
            if (pan.getCount() > getMaxCountPerStack()) {
                pan.setCount(getMaxCountPerStack());
            }
        } else if (slot > 0) {
            int materialIndex = slot - 1;
            while (materials.size() <= materialIndex) {
                materials.add(ItemStack.EMPTY);
            }
            ItemStack stackToSet = stack.copy();
            if (stackToSet.getCount() > getMaxCountPerStack()) {
                stackToSet.setCount(getMaxCountPerStack());
            }
            materials.set(materialIndex, stackToSet);
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
        pan = ItemStack.EMPTY;
        materials.clear();
        markDirty();
    }
}
