package com.hydroceder.hgbg.block.entity;

import com.hydroceder.hgbg.block.ModBlockEntityTypes;
import com.hydroceder.hgbg.recipe.mortar.MortarAndPestleRecipe;
import com.hydroceder.hgbg.recipe.ModRecipeTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * 研钵和杵方块实体类
 * 存储一个物品，用于研磨和加工
 */
public class MortarAndPestleBlockEntity extends BlockEntity implements Inventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    
    public MortarAndPestleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.MORTAR_AND_PESTLE_BLOCK_ENTITY, pos, state);
    }
    
    public ItemStack getItem(int slot) {
        if (slot == 0) {
            return items.get(slot);
        }
        return ItemStack.EMPTY;
    }
    
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            items.set(slot, stack);
            markDirty();
        }
    }
    
    public void dropItems() {
        if (world != null && !world.isClient) {
            ItemStack stack = items.get(0);
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, stack);
                world.spawnEntity(itemEntity);
            }
        }
    }
    
    public void ejectResult(ItemStack result) {
        if (world != null && !world.isClient) {
            ItemEntity itemEntity = new ItemEntity(
                world, 
                pos.getX() + 0.5, 
                pos.getY() + 1.0, 
                pos.getZ() + 0.5, 
                result
            );
            world.spawnEntity(itemEntity);
        }
    }
    
    public net.minecraft.util.ActionResult handleInteraction(PlayerEntity player, ItemStack heldStack) {
        if (world == null || world.isClient) {
            return net.minecraft.util.ActionResult.SUCCESS;
        }

        if (!heldStack.isEmpty()) {
            if (items.get(0).isEmpty()) {
                SimpleInventory inventory = new SimpleInventory(heldStack);
                Optional<MortarAndPestleRecipe> recipe = world.getRecipeManager()
                    .getAllMatches(ModRecipeTypes.MORTAR_AND_PESTLE_RECIPE_TYPE, inventory, world)
                    .stream()
                    .findFirst();

                if (recipe.isPresent()) {
                    ItemStack toStore = heldStack.copy();
                    toStore.setCount(1);
                    items.set(0, toStore);
                    heldStack.decrement(1);
                    markDirty();
                    return net.minecraft.util.ActionResult.SUCCESS;
                } else {
                    return net.minecraft.util.ActionResult.PASS;
                }
            } else if (!heldStack.isEmpty() && !items.get(0).isEmpty()) {
                SimpleInventory inventory = new SimpleInventory(items.get(0));
                Optional<MortarAndPestleRecipe> recipe = world.getRecipeManager()
                    .getAllMatches(ModRecipeTypes.MORTAR_AND_PESTLE_RECIPE_TYPE, inventory, world)
                    .stream()
                    .findFirst();

                if (recipe.isPresent() && recipe.get().requiresContainer() && heldStack.isOf(recipe.get().getRequiredContainer())) {
                    ItemStack result = recipe.get().getOutput().copy();
                    ejectResult(result);
                    heldStack.decrement(1);
                    items.set(0, ItemStack.EMPTY);
                    markDirty();
                    return net.minecraft.util.ActionResult.SUCCESS;
                }
            }
        } else {
            if (!items.get(0).isEmpty()) {
                SimpleInventory inventory = new SimpleInventory(items.get(0));
                Optional<MortarAndPestleRecipe> recipe = world.getRecipeManager()
                    .getAllMatches(ModRecipeTypes.MORTAR_AND_PESTLE_RECIPE_TYPE, inventory, world)
                    .stream()
                    .findFirst();

                if (recipe.isPresent()) {
                    if (recipe.get().requiresContainer()) {
                        player.sendMessage(net.minecraft.text.Text.translatable("block.hunger-begone.mortar.needs_container"), true);
                        return net.minecraft.util.ActionResult.FAIL;
                    }
                    ItemStack result = recipe.get().getOutput().copy();
                    ejectResult(result);
                    items.set(0, ItemStack.EMPTY);
                    markDirty();
                    return net.minecraft.util.ActionResult.SUCCESS;
                }
            }
        }

        return net.minecraft.util.ActionResult.PASS;
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, MortarAndPestleBlockEntity blockEntity) {
        // 研钵和杵不需要tick逻辑
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
    }
    
    @Override
    public int size() {
        return items.size();
    }
    
    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
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
        items.set(0, ItemStack.EMPTY);
    }
}