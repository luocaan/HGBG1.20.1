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
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StewPotBlockEntity extends BlockEntity implements Inventory {

    private static final int MAX_MATERIAL_SLOTS = 16;
    private final DefaultedList<ItemStack> inventoryItems = DefaultedList.ofSize(MAX_MATERIAL_SLOTS, ItemStack.EMPTY);
    private boolean hasLid = false;
    private boolean isCooking = false;
    private int cookTime = 0;
    private int totalCookTime = 0;
    private StewPotCookingRecipe matchedRecipe = null;
    private String matchedRecipeId = null;
    private UUID cookingPlayerUuid = null;

    public StewPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.STEW_POT_BLOCK_ENTITY, pos, state);
    }

    public boolean addMaterial(ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (int i = 0; i < MAX_MATERIAL_SLOTS; i++) {
            if (inventoryItems.get(i).isEmpty()) {
                inventoryItems.set(i, stack.copy());
                markDirty();
                return true;
            }
        }
        return false;
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
        inventoryItems.clear();
        hasLid = false;
        markDirty();
    }

    public boolean hasMaterialsOrLid() {
        for (ItemStack stack : inventoryItems) {
            if (!stack.isEmpty()) return true;
        }
        return hasLid;
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
        for (int i = 0; i < inventoryItems.size(); i++) {
            ItemStack item = inventoryItems.get(i);
            if (!item.isEmpty()) {
                rawMaterials.add(item.copy());
            }
        }

        if (rawMaterials.isEmpty()) {
            this.totalCookTime = 1;
            this.matchedRecipe = null;
            this.matchedRecipeId = null;
            return;
        }

        StewPotRecipeManager.MatchResult matchResult =
            StewPotRecipeManager.findRecipe(rawMaterials).orElse(null);

        if (matchResult != null && matchResult.recipe != null) {
            this.totalCookTime = matchResult.recipe.getCookTime();
            this.matchedRecipe = matchResult.recipe;
            this.matchedRecipeId = matchResult.recipe.getId().toString();
        } else {
            this.totalCookTime = 200;
            this.matchedRecipe = null;
            this.matchedRecipeId = null;
        }
    }

    public void stopCooking() {
        this.isCooking = false;
        this.cookTime = 0;
        this.totalCookTime = 0;
        this.matchedRecipe = null;
        this.matchedRecipeId = null;
        this.cookingPlayerUuid = null;
        markDirty();
    }

    public void dropItems() {
        if (world != null && !world.isClient) {
            for (int i = 0; i < inventoryItems.size(); i++) {
                ItemStack item = inventoryItems.get(i);
                if (!item.isEmpty()) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), item.copy());
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
        for (int i = 0; i < be.inventoryItems.size(); i++) {
            ItemStack item = be.inventoryItems.get(i);
            if (!item.isEmpty() && !(item.getItem() instanceof PotLidItem)) {
                rawMaterials.add(item.copy());
            }
        }

        if (rawMaterials.isEmpty()) {
            if (be.hasLid) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(ModItems.POT_LID));
            }
            be.inventoryItems.clear();
            be.hasLid = false;
            be.matchedRecipe = null;
            be.matchedRecipeId = null;
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
                createSuspiciousStewWithEffects(world));
        }

        if (be.hasLid) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(),
                new ItemStack(ModItems.POT_LID));
        }

        be.inventoryItems.clear();
        be.hasLid = false;
        be.matchedRecipe = null;
        be.matchedRecipeId = null;
        world.setBlockState(pos, state.with(StewPotBlock.STEW_STATE, StewPotBlock.StewState.EMPTY), 3);
        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
            SoundCategory.BLOCKS, 1.0f, 1.0f);
        be.markDirty();
    }

    private static final StatusEffect[][] SUSPICIOUS_STEW_EFFECTS = {
        {StatusEffects.NIGHT_VISION},
        {StatusEffects.JUMP_BOOST},
        {StatusEffects.WEAKNESS},
        {StatusEffects.BLINDNESS},
        {StatusEffects.POISON},
        {StatusEffects.SATURATION},
        {StatusEffects.FIRE_RESISTANCE},
        {StatusEffects.REGENERATION}
    };

    private static final int[] SUSPICIOUS_STEW_DURATIONS = {
        160,
        160,
        400,
        120,
        320,
        160,
        160,
        200
    };

    private static ItemStack createSuspiciousStewWithEffects(World world) {
        ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);
        NbtCompound nbt = new NbtCompound();
        NbtList effects = new NbtList();

        int effectCount = 1 + world.random.nextInt(2);
        List<Integer> availableIndices = new ArrayList<>();
        for (int i = 0; i < SUSPICIOUS_STEW_EFFECTS.length; i++) {
            availableIndices.add(i);
        }

        for (int i = 0; i < effectCount && !availableIndices.isEmpty(); i++) {
            int idx = world.random.nextInt(availableIndices.size());
            int chosenIndex = availableIndices.remove(idx);

            NbtCompound effectEntry = new NbtCompound();
            effectEntry.putString("id",
                Registries.STATUS_EFFECT.getId(SUSPICIOUS_STEW_EFFECTS[chosenIndex][0]).toString());
            effectEntry.putInt("duration", SUSPICIOUS_STEW_DURATIONS[chosenIndex]);
            effects.add(effectEntry);
        }

        if (!effects.isEmpty()) {
            nbt.put("effects", effects);
            stew.setNbt(nbt);
        }

        return stew;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventoryItems);
        nbt.putBoolean("HasLid", hasLid);
        nbt.putBoolean("IsCooking", isCooking);
        nbt.putInt("CookTime", cookTime);
        nbt.putInt("TotalCookTime", totalCookTime);
        if (matchedRecipeId != null) {
            nbt.putString("MatchedRecipeId", matchedRecipeId);
        }
        if (cookingPlayerUuid != null) {
            nbt.putUuid("CookingPlayerUuid", cookingPlayerUuid);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inventoryItems.clear();
        Inventories.readNbt(nbt, inventoryItems);
        hasLid = nbt.getBoolean("HasLid");
        isCooking = nbt.getBoolean("IsCooking");
        cookTime = nbt.getInt("CookTime");
        totalCookTime = nbt.getInt("TotalCookTime");
        if (nbt.contains("MatchedRecipeId")) {
            matchedRecipeId = nbt.getString("MatchedRecipeId");
            StewPotRecipeManager.getRecipeById(new net.minecraft.util.Identifier(matchedRecipeId))
                .ifPresent(recipe -> this.matchedRecipe = recipe);
        }
        if (nbt.contains("CookingPlayerUuid")) {
            cookingPlayerUuid = nbt.getUuid("CookingPlayerUuid");
        }
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
        return inventoryItems.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventoryItems) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot >= 0 && slot < inventoryItems.size()) {
            return inventoryItems.get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(inventoryItems, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventoryItems, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < inventoryItems.size()) {
            inventoryItems.set(slot, stack);
            if (stack.getCount() > getMaxCountPerStack()) {
                stack.setCount(getMaxCountPerStack());
            }
            markDirty();
        }
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
        inventoryItems.clear();
        markDirty();
    }
}
