package com.hydroceder.hgbg.block.entity;

import com.hydroceder.hgbg.block.ModBlockEntityTypes;
import com.hydroceder.hgbg.recipe.ModRecipeTypes;
import com.hydroceder.hgbg.recipe.oven.OvenRecipe;
import com.hydroceder.hgbg.recipe.mortar.MortarAndPestleRecipe;
import com.hydroceder.hgbg.recipe.pan_cooking.PanCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ShelfBlockEntity extends BlockEntity implements Inventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);
    private static final Random random = new Random();
    
    public ShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SHELF_BLOCK_ENTITY, pos, state);
    }
    
    public boolean addItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        for (int i = 0; i < 9; i++) {
            if (items.get(i).isEmpty()) {
                ItemStack singleItem = stack.copy();
                singleItem.setCount(1);
                items.set(i, singleItem);
                markDirty();
                return true;
            }
        }
        
        return false;
    }
    
    public ItemStack removeLast() {
        for (int i = 8; i >= 0; i--) {
            if (!items.get(i).isEmpty()) {
                ItemStack stack = items.get(i).copy();
                items.set(i, ItemStack.EMPTY);
                markDirty();
                return stack;
            }
        }
        
        return ItemStack.EMPTY;
    }
    
    public void clearAll() {
        for (int i = 0; i < 9; i++) {
            items.set(i, ItemStack.EMPTY);
        }
        markDirty();
    }
    
    public List<ItemStack> getItems() {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                result.add(stack.copy());
            }
        }
        return result;
    }
    
    public void dropItems(World world, BlockPos pos) {
        if (world != null && !world.isClient) {
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, stack);
                    world.spawnEntity(itemEntity);
                }
            }
        }
    }
    
    public void updateHint(PlayerEntity player) {
        if (world == null || world.isClient) {
            return;
        }
        
        List<ItemStack> currentItems = getItems();
        
        List<Recipe<?>> allRecipes = new ArrayList<>();
        allRecipes.addAll(world.getRecipeManager().listAllOfType(ModRecipeTypes.OVEN_RECIPE_TYPE));
        allRecipes.addAll(world.getRecipeManager().listAllOfType(ModRecipeTypes.PAN_COOKING_RECIPE_TYPE));
        allRecipes.addAll(world.getRecipeManager().listAllOfType(ModRecipeTypes.MORTAR_AND_PESTLE_RECIPE_TYPE));
        
        List<Recipe<?>> matchingRecipes = new ArrayList<>();
        
        for (Recipe<?> recipe : allRecipes) {
            if (doesMatchRecipe(currentItems, recipe)) {
                matchingRecipes.add(recipe);
            }
        }
        
        if (matchingRecipes.isEmpty()) {
            player.sendMessage(Text.translatable("shelf.hint.no_recipe").formatted(Formatting.RED), true);
        } else {
            Recipe<?> selectedRecipe = matchingRecipes.get(random.nextInt(matchingRecipes.size()));
            List<ItemStack> recipeInputs = getRecipeInputs(selectedRecipe);
            int currentTotal = currentItems.size();
            int requiredTotal = recipeInputs.size();
            
            if (currentTotal == requiredTotal) {
                player.sendMessage(Text.translatable("shelf.hint.full_recipe", getToolName(selectedRecipe)).formatted(Formatting.GREEN), true);
            } else {
                ItemStack missingItem = findMissingItem(currentItems, selectedRecipe);
                if (missingItem != null) {
                    player.sendMessage(Text.translatable("shelf.hint.missing_item", missingItem.getName()).formatted(Formatting.YELLOW), true);
                }
            }
        }
    }
    
    private boolean doesMatchRecipe(List<ItemStack> currentItems, Recipe<?> recipe) {
        List<ItemStack> remainingItems = new ArrayList<>();
        for (ItemStack stack : currentItems) {
            remainingItems.add(stack.copy());
        }
        
        if (recipe instanceof OvenRecipe) {
            OvenRecipe ovenRecipe = (OvenRecipe) recipe;
            List<net.minecraft.recipe.Ingredient> remainingIngredients = new ArrayList<>(ovenRecipe.getIngredients());
            
            for (ItemStack currentItem : new ArrayList<>(remainingItems)) {
                if (currentItem.isEmpty()) continue;
                
                boolean found = false;
                java.util.Iterator<net.minecraft.recipe.Ingredient> iterator = remainingIngredients.iterator();
                while (iterator.hasNext()) {
                    net.minecraft.recipe.Ingredient ingredient = iterator.next();
                    if (ingredient.test(currentItem)) {
                        iterator.remove();
                        remainingItems.remove(currentItem);
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    return false;
                }
            }
            return true;
        } else if (recipe instanceof PanCookingRecipe) {
            PanCookingRecipe panRecipe = (PanCookingRecipe) recipe;
            List<ItemStack> requiredInputs = panRecipe.getInputs();
            Map<Item, Integer> requiredCounts = toCountMap(requiredInputs);
            Map<Item, Integer> currentCounts = toCountMap(currentItems);
            
            for (Map.Entry<Item, Integer> entry : currentCounts.entrySet()) {
                Item item = entry.getKey();
                int current = entry.getValue();
                int required = requiredCounts.getOrDefault(item, 0);
                if (current > required) {
                    return false;
                }
            }
            return true;
        } else if (recipe instanceof MortarAndPestleRecipe) {
            MortarAndPestleRecipe mortarRecipe = (MortarAndPestleRecipe) recipe;
            if (currentItems.size() != 1) {
                return currentItems.isEmpty();
            }
            return mortarRecipe.getIngredient().test(currentItems.get(0));
        }
        return false;
    }
    
    private ItemStack findMissingItem(List<ItemStack> currentItems, Recipe<?> recipe) {
        if (recipe instanceof OvenRecipe) {
            OvenRecipe ovenRecipe = (OvenRecipe) recipe;
            List<net.minecraft.recipe.Ingredient> ingredients = ovenRecipe.getIngredients();
            List<ItemStack> remainingItems = new ArrayList<>();
            for (ItemStack stack : currentItems) {
                remainingItems.add(stack.copy());
            }
            
            for (net.minecraft.recipe.Ingredient ingredient : ingredients) {
                boolean found = false;
                java.util.Iterator<ItemStack> iterator = remainingItems.iterator();
                while (iterator.hasNext()) {
                    ItemStack item = iterator.next();
                    if (ingredient.test(item)) {
                        iterator.remove();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ItemStack[] matchingStacks = ingredient.getMatchingStacks();
                    if (matchingStacks.length > 0) {
                        return matchingStacks[0].copy();
                    }
                }
            }
        } else if (recipe instanceof PanCookingRecipe) {
            PanCookingRecipe panRecipe = (PanCookingRecipe) recipe;
            List<ItemStack> requiredInputs = panRecipe.getInputs();
            Map<Item, Integer> requiredCounts = toCountMap(requiredInputs);
            Map<Item, Integer> currentCounts = toCountMap(currentItems);
            List<Item> missingItems = new ArrayList<>();
            
            for (Map.Entry<Item, Integer> entry : requiredCounts.entrySet()) {
                Item item = entry.getKey();
                int need = entry.getValue();
                int have = currentCounts.getOrDefault(item, 0);
                for (int i = 0; i < need - have; i++) {
                    missingItems.add(item);
                }
            }
            
            if (!missingItems.isEmpty()) {
                Item item = missingItems.get(random.nextInt(missingItems.size()));
                return new ItemStack(item);
            }
        } else if (recipe instanceof MortarAndPestleRecipe) {
            MortarAndPestleRecipe mortarRecipe = (MortarAndPestleRecipe) recipe;
            if (currentItems.isEmpty()) {
                ItemStack[] matchingStacks = mortarRecipe.getIngredient().getMatchingStacks();
                if (matchingStacks.length > 0) {
                    return matchingStacks[0].copy();
                }
            }
        }
        return null;
    }
    
    private List<ItemStack> getRecipeInputs(Recipe<?> recipe) {
        if (recipe instanceof OvenRecipe) {
            List<ItemStack> inputs = new ArrayList<>();
            OvenRecipe ovenRecipe = (OvenRecipe) recipe;
            for (net.minecraft.recipe.Ingredient ingredient : ovenRecipe.getIngredients()) {
                ItemStack[] matchingStacks = ingredient.getMatchingStacks();
                if (matchingStacks.length > 0) {
                    ItemStack stack = matchingStacks[0].copy();
                    stack.setCount(1);
                    inputs.add(stack);
                }
            }
            return inputs;
        } else if (recipe instanceof PanCookingRecipe) {
            return ((PanCookingRecipe) recipe).getInputs();
        } else if (recipe instanceof MortarAndPestleRecipe) {
            List<ItemStack> inputs = new ArrayList<>();
            MortarAndPestleRecipe mortarRecipe = (MortarAndPestleRecipe) recipe;
            ItemStack[] matchingStacks = mortarRecipe.getIngredient().getMatchingStacks();
            if (matchingStacks.length > 0) {
                ItemStack stack = matchingStacks[0].copy();
                stack.setCount(1);
                inputs.add(stack);
            }
            return inputs;
        }
        return new ArrayList<>();
    }
    
    private String getToolName(Recipe<?> recipe) {
        if (recipe instanceof OvenRecipe) {
            return Text.translatable("shelf.tool.oven").getString();
        } else if (recipe instanceof PanCookingRecipe) {
            return Text.translatable("shelf.tool.pan").getString();
        } else if (recipe instanceof MortarAndPestleRecipe) {
            return Text.translatable("shelf.tool.mortar").getString();
        }
        return "厨具";
    }
    
    private Map<Item, Integer> toCountMap(List<ItemStack> items) {
        Map<Item, Integer> map = new HashMap<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                map.merge(stack.getItem(), 1, Integer::sum);
            }
        }
        return map;
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
        items.clear();
    }
}
