package com.hydroceder.hgbg.block.entity;

import com.hydroceder.hgbg.block.ModBlockEntityTypes;
import com.hydroceder.hgbg.seasoning.Seasoning;
import com.hydroceder.hgbg.seasoning.SeasoningRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CupBlockEntity extends BlockEntity implements Inventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public CupBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CUP_BLOCK_ENTITY, pos, state);
    }

    public boolean hasSeasoning() {
        return !items.get(0).isEmpty();
    }

    public ItemStack getStoredSeasoning() {
        return items.get(0);
    }

    public Seasoning getSeasoningData() {
        ItemStack stack = items.get(0);
        if (stack.isEmpty()) return null;
        return SeasoningRegistry.getSeasoning(stack.getItem());
    }

    public boolean canStore(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Seasoning seasoning = SeasoningRegistry.getSeasoning(stack.getItem());
        if (seasoning == null || !seasoning.isStorable()) return false;

        if (isEmpty()) {
            return true;
        }

        ItemStack stored = items.get(0);
        return stored.getItem() == stack.getItem();
    }

    public boolean storeSeasoning(ItemStack stack) {
        if (!canStore(stack)) return false;

        if (isEmpty()) {
            ItemStack singleItem = stack.copy();
            singleItem.setCount(1);
            items.set(0, singleItem);
        } else {
            items.get(0).increment(1);
        }
        markDirty();
        return true;
    }

    public boolean canTakeOut(PlayerEntity player) {
        if (isEmpty()) return false;
        Seasoning seasoning = getSeasoningData();
        if (seasoning == null) return false;
        if (seasoning.isBottled()) {
            return player.getInventory().contains(new ItemStack(net.minecraft.item.Items.GLASS_BOTTLE));
        }
        return true;
    }

    public ItemStack takeOut(PlayerEntity player) {
        if (isEmpty()) return ItemStack.EMPTY;

        ItemStack stored = items.get(0);
        ItemStack result = stored.copy();
        result.setCount(1);

        if (stored.getCount() <= 1) {
            items.set(0, ItemStack.EMPTY);
        } else {
            stored.decrement(1);
        }
        markDirty();
        return result;
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

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public int size() {
        return 1;
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
        items.clear();
    }
}