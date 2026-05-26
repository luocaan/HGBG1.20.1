package com.hydroceder.hgbg.block.entity;

import com.hydroceder.hgbg.block.ModBlockEntityTypes;
import com.hydroceder.hgbg.block.StewPotBlock;
import com.hydroceder.hgbg.item.ModItems;
import com.hydroceder.hgbg.item.tool.PotLidItem;
import com.hydroceder.hgbg.recipe.stew_pot_cooking.StewPotCookingRecipe;
import com.hydroceder.hgbg.recipe.stew_pot_cooking.StewPotRecipeManager;
import com.hydroceder.hgbg.util.SeasoningNBT;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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

    private NbtList materials = new NbtList();
    private boolean hasLid = false;
    private boolean isCooking = false;
    private int cookTime = 0;
    private int totalCookTime = 0;
    private StewPotCookingRecipe matchedRecipe = null;
    private UUID cookingPlayerUuid = null;

    public StewPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.STEW_POT_BLOCK_ENTITY, pos, state);
    }

    public void addMaterial(ItemStack stack) {
        if (stack.isEmpty()) return;
        NbtCompound itemNbt = new NbtCompound();
        stack.writeNbt(itemNbt);
        materials.add(itemNbt);
        markDirty();
    }

    public void addLid() {
        hasLid = true;
        markDirty();
    }

    public boolean hasLid() {
        return hasLid;
    }

    public boolean isCooking() {
        return isCooking;
    }

    public void clearMaterials() {
        materials.clear();
        hasLid = false;
        markDirty();
    }

    public boolean hasMaterialsOrLid() {
        return !materials.isEmpty() || hasLid;
    }

    public void startCooking(UUID playerUuid) {
        this.isCooking = true;
        this.cookTime = 0;
        this.cookingPlayerUuid = playerUuid;
        matchAndSetRecipe();
        markDirty();
    }

    private void matchAndSetRecipe() {
        List<ItemStack> rawMaterials = new ArrayList<>();
        for (int i = 0; i < materials.size(); i++) {
            ItemStack item = ItemStack.fromNbt(materials.getCompound(i));
            if (!item.isEmpty()) {
                rawMaterials.add(item);
            }
        }

        if (rawMaterials.isEmpty()) {
            this.totalCookTime = 1;
            this.matchedRecipe = null;
            return;
        }

        StewPotRecipeManager.MatchResult matchResult =
            StewPotRecipeManager.findRecipe(rawMaterials).orElse(null);

        if (matchResult != null && matchResult.recipe != null) {
            this.totalCookTime = matchResult.recipe.getCookTime();
            this.matchedRecipe = matchResult.recipe;
        } else {
            this.totalCookTime = 200;
            this.matchedRecipe = null;
        }
    }

    public void stopCooking() {
        this.isCooking = false;
        this.cookTime = 0;
        this.totalCookTime = 0;
        this.matchedRecipe = null;
        this.cookingPlayerUuid = null;
        markDirty();
    }

    public void dropItems() {
        if (world != null && !world.isClient) {
            for (int i = 0; i < materials.size(); i++) {
                ItemStack item = ItemStack.fromNbt(materials.getCompound(i));
                if (!item.isEmpty()) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), item);
                }
            }
            if (hasLid) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(ModItems.POT_LID));
            }
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

            int remainingTicks = be.totalCookTime - be.cookTime;
            int remainingSeconds = Math.max(0, (remainingTicks + 19) / 20);
            if (be.cookingPlayerUuid != null) {
                PlayerEntity cookingPlayer = world.getPlayerByUuid(be.cookingPlayerUuid);
                if (cookingPlayer != null) {
                    cookingPlayer.sendMessage(
                        Text.translatable("stewpot.cooking", remainingSeconds), true);
                }
            }

            if (be.cookTime >= be.totalCookTime) {
                finishCooking(world, pos, state, be);
                be.stopCooking();
            }
            be.markDirty();
        }
    }

    private static void finishCooking(World world, BlockPos pos, BlockState state, StewPotBlockEntity be) {
        List<ItemStack> rawMaterials = new ArrayList<>();
        for (int i = 0; i < be.materials.size(); i++) {
            ItemStack item = ItemStack.fromNbt(be.materials.getCompound(i));
            if (!item.isEmpty() && !(item.getItem() instanceof PotLidItem)) {
                rawMaterials.add(item);
            }
        }

        if (rawMaterials.isEmpty()) {
            if (be.hasLid) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(ModItems.POT_LID));
            }
            be.materials.clear();
            be.hasLid = false;
            be.matchedRecipe = null;
            world.setBlockState(pos, state.with(StewPotBlock.STEW_STATE, StewPotBlock.StewState.EMPTY), 3);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
                SoundCategory.BLOCKS, 1.0f, 1.0f);
            be.markDirty();
            return;
        }

        StewPotRecipeManager.MatchResult matchResult = null;
        if (be.matchedRecipe != null) {
            SeasoningNBT.SeparationResult sep = SeasoningNBT.separateSeasonings(rawMaterials);
            matchResult = new StewPotRecipeManager.MatchResult(be.matchedRecipe, 1, sep.seasoningIds);
        } else {
            matchResult = StewPotRecipeManager.findRecipe(rawMaterials).orElse(null);
        }

        if (matchResult != null && matchResult.recipe != null) {
            List<ItemStack> outputs = matchResult.recipe.getOutputs(matchResult.scaleFactor);
            if (!matchResult.seasoningIds.isEmpty()) {
                for (ItemStack output : outputs) {
                    SeasoningNBT.addSeasonings(output, matchResult.seasoningIds);
                }
            }
            for (ItemStack output : outputs) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(),
                    output.copy());
            }
        } else {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(),
                new ItemStack(Items.SUSPICIOUS_STEW));
        }

        if (be.hasLid) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(),
                new ItemStack(ModItems.POT_LID));
        }

        be.materials.clear();
        be.hasLid = false;
        be.matchedRecipe = null;
        world.setBlockState(pos, state.with(StewPotBlock.STEW_STATE, StewPotBlock.StewState.EMPTY), 3);
        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
            SoundCategory.BLOCKS, 1.0f, 1.0f);
        be.markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("Materials", materials);
        nbt.putBoolean("HasLid", hasLid);
        nbt.putBoolean("IsCooking", isCooking);
        nbt.putInt("CookTime", cookTime);
        nbt.putInt("TotalCookTime", totalCookTime);
        if (cookingPlayerUuid != null) {
            nbt.putUuid("CookingPlayerUuid", cookingPlayerUuid);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Materials")) {
            materials = nbt.getList("Materials", 10);
        } else {
            materials = new NbtList();
        }
        hasLid = nbt.getBoolean("HasLid");
        isCooking = nbt.getBoolean("IsCooking");
        cookTime = nbt.getInt("CookTime");
        totalCookTime = nbt.getInt("TotalCookTime");
        if (nbt.contains("CookingPlayerUuid")) {
            cookingPlayerUuid = nbt.getUuid("CookingPlayerUuid");
        }
    }
}