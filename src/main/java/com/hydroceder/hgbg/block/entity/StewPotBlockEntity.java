package com.hydroceder.hgbg.block.entity;

import com.hydroceder.hgbg.block.ModBlockEntityTypes;
import com.hydroceder.hgbg.block.StewPotBlock;
import com.hydroceder.hgbg.item.tool.BasketItem;
import com.hydroceder.hgbg.recipe.pan_cooking.PanCookingRecipeManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StewPotBlockEntity extends BlockEntity {

    private NbtList baskets = new NbtList();
    private boolean isCooking = false;
    private int cookTime = 0;
    private static final int TOTAL_COOK_TIME = 200;
    private UUID cookingPlayerUuid = null;

    public StewPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.STEW_POT_BLOCK_ENTITY, pos, state);
    }

    public void addBasket(ItemStack basketStack) {
        if (basketStack.isEmpty() || !(basketStack.getItem() instanceof BasketItem)) {
            return;
        }
        NbtCompound basketNbt = basketStack.getNbt();
        if (basketNbt != null && basketNbt.contains(BasketItem.getStoredKey())) {
            baskets.add(0, basketNbt.copy());
        } else {
            NbtCompound emptyBasket = new NbtCompound();
            baskets.add(0, emptyBasket);
        }
        markDirty();
        updateContentsState();
    }

    public int getBasketCount() {
        return baskets.size();
    }

    public boolean hasBaskets() {
        return !baskets.isEmpty();
    }

    public void clearBaskets() {
        baskets.clear();
        markDirty();
        updateContentsState();
    }

    public boolean isCooking() {
        return isCooking;
    }

    public void startCooking(UUID playerUuid) {
        this.isCooking = true;
        this.cookTime = 0;
        this.cookingPlayerUuid = playerUuid;
        markDirty();
    }

    public void stopCooking() {
        this.isCooking = false;
        this.cookTime = 0;
        this.cookingPlayerUuid = null;
        markDirty();
    }

    public void dropItems() {
        if (world != null && !world.isClient && !baskets.isEmpty()) {
            for (int i = 0; i < baskets.size(); i++) {
                ItemStack basketStack = new ItemStack(com.hydroceder.hgbg.item.ModItems.BASKET);
                NbtCompound basketData = baskets.getCompound(i);
                if (!basketData.isEmpty()) {
                    basketStack.setNbt(basketData);
                }
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), basketStack);
            }
        }
    }

    private void updateContentsState() {
        if (world != null && !world.isClient) {
            BlockState state = getCachedState();
            boolean hasContents = !baskets.isEmpty();
            world.setBlockState(pos, state.with(StewPotBlock.HAS_CONTENTS, hasContents), 3);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, StewPotBlockEntity be) {
        if (world.isClient) return;

        if (be.isCooking) {
            be.cookTime++;

            if (be.cookTime % 20 == 0) {
                world.playSound(null, pos, SoundEvents.BLOCK_FIRE_AMBIENT,
                    SoundCategory.BLOCKS, 1.2f, 1.0f);
            }

            int remainingSeconds = (TOTAL_COOK_TIME - be.cookTime + 19) / 20;
            if (be.cookingPlayerUuid != null) {
                PlayerEntity cookingPlayer = world.getPlayerByUuid(be.cookingPlayerUuid);
                if (cookingPlayer != null) {
                    cookingPlayer.sendMessage(
                        Text.translatable("stewpot.cooking", remainingSeconds), true);
                }
            }

            if (be.cookTime >= TOTAL_COOK_TIME) {
                finishCooking(world, pos, state, be);
                be.stopCooking();
            }
            be.markDirty();
        }
    }

    private static void finishCooking(World world, BlockPos pos, BlockState state, StewPotBlockEntity be) {
        List<ItemStack> outputs = new ArrayList<>();
        List<ItemStack> emptyBaskets = new ArrayList<>();

        for (int i = 0; i < be.baskets.size(); i++) {
            NbtCompound basketData = be.baskets.getCompound(i);

            emptyBaskets.add(new ItemStack(com.hydroceder.hgbg.item.ModItems.BASKET));

            if (basketData.isEmpty()) {
                outputs.add(new ItemStack(Items.CHARCOAL));
                continue;
            }

            List<ItemStack> materials = extractMaterialsFromBasket(basketData);
            if (materials.isEmpty()) {
                outputs.add(new ItemStack(Items.CHARCOAL));
                continue;
            }

            PanCookingRecipeManager.MatchResult matchResult =
                PanCookingRecipeManager.findRecipe(materials).orElse(null);

            if (matchResult != null && matchResult.recipe != null) {
                outputs.addAll(matchResult.recipe.getOutputs(matchResult.scaleFactor));
                if (!matchResult.seasoningIds.isEmpty()) {
                    for (ItemStack output : outputs) {
                        com.hydroceder.hgbg.util.SeasoningNBT
                            .addSeasonings(output, matchResult.seasoningIds);
                    }
                }
            } else {
                outputs.add(new ItemStack(Items.CHARCOAL));
            }
        }

        for (ItemStack output : outputs) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), output.copy());
        }
        for (ItemStack emptyBasket : emptyBaskets) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), emptyBasket);
        }

        be.baskets.clear();
        world.setBlockState(pos, state.with(StewPotBlock.HAS_CONTENTS, false), 3);
        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
            SoundCategory.BLOCKS, 1.0f, 1.0f);
        be.markDirty();
    }

    private static List<ItemStack> extractMaterialsFromBasket(NbtCompound basketData) {
        List<ItemStack> materials = new ArrayList<>();
        if (!basketData.contains(BasketItem.getStoredKey())) {
            return materials;
        }
        NbtList storedList = basketData.getList(BasketItem.getStoredKey(), 10);
        for (int i = 0; i < storedList.size(); i++) {
            ItemStack item = ItemStack.fromNbt(storedList.getCompound(i));
            if (!item.isEmpty()) {
                materials.add(item);
            }
        }
        return materials;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Baskets", baskets);
        nbt.putBoolean("IsCooking", isCooking);
        nbt.putInt("CookTime", cookTime);
        if (cookingPlayerUuid != null) {
            nbt.putUuid("CookingPlayerUuid", cookingPlayerUuid);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Baskets")) {
            baskets = nbt.getList("Baskets", 10);
        } else {
            baskets = new NbtList();
        }
        isCooking = nbt.getBoolean("IsCooking");
        cookTime = nbt.getInt("CookTime");
        if (nbt.contains("CookingPlayerUuid")) {
            cookingPlayerUuid = nbt.getUuid("CookingPlayerUuid");
        }
    }
}
