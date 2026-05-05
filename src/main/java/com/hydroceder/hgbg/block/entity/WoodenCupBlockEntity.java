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

public class WoodenCupBlockEntity extends BlockEntity implements Inventory {
    private static final int MAX_CUPS = 3;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(MAX_CUPS, ItemStack.EMPTY);
    private int cupCount = 1;

    public WoodenCupBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.WOODEN_CUP_BLOCK_ENTITY, pos, state);
    }

    public int getCupCount() {
        return cupCount;
    }

    public void setCupCount(int count) {
        this.cupCount = Math.min(MAX_CUPS, Math.max(1, count));
        markDirty();
    }

    public boolean canAddCup() {
        return cupCount < MAX_CUPS;
    }

    public int getUsedSlots() {
        int count = 0;
        for (int i = 0; i < cupCount; i++) {
            if (!items.get(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public boolean hasSeasoning() {
        for (int i = 0; i < cupCount; i++) {
            if (!items.get(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean itemsAreEmpty(int slot) {
        if (slot >= cupCount || slot < 0) return true;
        return items.get(slot).isEmpty();
    }

    public int findEmptySlot() {
        for (int i = 0; i < cupCount; i++) {
            if (items.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public int findMatchingSlot(ItemStack stack) {
        Item item = stack.getItem();
        for (int i = 0; i < cupCount; i++) {
            if (!items.get(i).isEmpty() && items.get(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public Seasoning getSeasoningData(int slot) {
        if (slot >= cupCount || slot < 0) return null;
        ItemStack stack = items.get(slot);
        if (stack.isEmpty()) return null;
        return SeasoningRegistry.getSeasoning(stack.getItem());
    }

    public boolean canStore(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Seasoning seasoning = SeasoningRegistry.getSeasoning(stack.getItem());
        if (seasoning == null || !seasoning.isStorable()) return false;

        int matchingSlot = findMatchingSlot(stack);
        if (matchingSlot >= 0) return true;

        return findEmptySlot() >= 0;
    }

    public boolean storeSeasoning(ItemStack stack) {
        if (!canStore(stack)) return false;

        int slot = findMatchingSlot(stack);
        if (slot >= 0) {
            items.get(slot).increment(1);
        } else {
            slot = findEmptySlot();
            if (slot >= 0) {
                ItemStack singleItem = stack.copy();
                singleItem.setCount(1);
                items.set(slot, singleItem);
            } else {
                return false;
            }
        }
        markDirty();
        return true;
    }

    public boolean canTakeOut(PlayerEntity player, int slot) {
        if (slot >= cupCount || slot < 0) return false;
        if (items.get(slot).isEmpty()) return false;
        Seasoning seasoning = getSeasoningData(slot);
        if (seasoning == null) return false;
        if (seasoning.isBottled()) {
            return player.getInventory().contains(new ItemStack(net.minecraft.item.Items.GLASS_BOTTLE));
        }
        return true;
    }

    public ItemStack takeOut(int slot) {
        if (slot >= cupCount || slot < 0) return ItemStack.EMPTY;
        if (items.get(slot).isEmpty()) return ItemStack.EMPTY;

        ItemStack stored = items.get(slot);
        ItemStack result = stored.copy();
        result.setCount(1);

        if (stored.getCount() <= 1) {
            items.set(slot, ItemStack.EMPTY);
        } else {
            stored.decrement(1);
        }
        markDirty();
        return result;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("CupCount", cupCount);
        Inventories.writeNbt(nbt, items);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.cupCount = nbt.getInt("CupCount");
        if (this.cupCount < 1) this.cupCount = 1;
        if (this.cupCount > MAX_CUPS) this.cupCount = MAX_CUPS;
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
        return MAX_CUPS;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            if (!items.get(i).isEmpty()) {
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
        items.clear();
    }
}
