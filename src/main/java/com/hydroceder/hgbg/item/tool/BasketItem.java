package com.hydroceder.hgbg.item.tool;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class BasketItem extends Item {

    private static final String STORED_KEY = "StoredItems";
    private static final int MAX_CAPACITY = 6;

    public static String getStoredKey() {
        return STORED_KEY;
    }

    public BasketItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.isSneaking()) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        ItemStack basketStack = user.getStackInHand(hand);
        Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack otherStack = user.getStackInHand(otherHand);

        if (otherStack.isEmpty()) {
            return retrieveItems(world, user, hand, basketStack);
        } else {
            return storeItems(world, user, otherHand, basketStack, otherStack);
        }
    }

    private TypedActionResult<ItemStack> storeItems(World world, PlayerEntity user, Hand itemHand, ItemStack basketStack, ItemStack itemStack) {
        if (world.isClient) {
            return TypedActionResult.success(basketStack);
        }

        if (itemStack.getItem() instanceof BasketItem) {
            user.sendMessage(Text.translatable("item.hunger-begone.basket.cannot_store_basket").formatted(Formatting.RED), true);
            return TypedActionResult.fail(basketStack);
        }

        NbtCompound nbt = basketStack.getOrCreateNbt();
        NbtList storedList;
        if (nbt.contains(STORED_KEY)) {
            storedList = nbt.getList(STORED_KEY, 10);
        } else {
            storedList = new NbtList();
        }

        if (storedList.size() >= MAX_CAPACITY) {
            user.sendMessage(Text.translatable("item.hunger-begone.basket.full").formatted(Formatting.RED), true);
            return TypedActionResult.fail(basketStack);
        }

        NbtCompound itemNbt = new NbtCompound();
        itemStack.writeNbt(itemNbt);
        storedList.add(0, itemNbt);
        nbt.put(STORED_KEY, storedList);

        user.setStackInHand(itemHand, ItemStack.EMPTY);
        user.getItemCooldownManager().set(this, 10);

        return TypedActionResult.success(basketStack);
    }

    private TypedActionResult<ItemStack> retrieveItems(World world, PlayerEntity user, Hand hand, ItemStack basketStack) {
        if (world.isClient) {
            return TypedActionResult.success(basketStack);
        }
        NbtCompound nbt = basketStack.getOrCreateNbt();
        if (!nbt.contains(STORED_KEY)) {
            return TypedActionResult.pass(basketStack);
        }

        NbtList storedList = nbt.getList(STORED_KEY, 10);
        if (storedList.isEmpty()) {
            return TypedActionResult.pass(basketStack);
        }

        ItemStack retrieved = ItemStack.fromNbt(storedList.getCompound(0));
        storedList.remove(0);

        if (storedList.isEmpty()) {
            nbt.remove(STORED_KEY);
        }

        Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (user.getStackInHand(otherHand).isEmpty()) {
            user.setStackInHand(otherHand, retrieved);
        } else {
            if (!user.giveItemStack(retrieved)) {
                user.dropItem(retrieved, false);
            }
        }

        user.getItemCooldownManager().set(this, 10);

        return TypedActionResult.success(basketStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(STORED_KEY)) {
            NbtList storedList = nbt.getList(STORED_KEY, 10);
            tooltip.add(Text.translatable("item.hunger-begone.basket.stored_header",
                storedList.size()
            ).formatted(Formatting.GRAY));
            for (int i = 0; i < storedList.size(); i++) {
                ItemStack stored = ItemStack.fromNbt(storedList.getCompound(i));
                tooltip.add(Text.literal("  - ")
                    .append(stored.getName())
                    .append(Text.literal(" x" + stored.getCount()))
                    .formatted(Formatting.GRAY));
            }
        } else {
            tooltip.add(Text.translatable("item.hunger-begone.basket.empty").formatted(Formatting.GRAY));
        }
    }

    public static boolean hasStoredItems(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(STORED_KEY)) {
            return false;
        }
        return !nbt.getList(STORED_KEY, 10).isEmpty();
    }
}
