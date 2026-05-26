package com.hydroceder.hgbg.recipe.stew_pot_cooking;

import com.hydroceder.hgbg.recipe.ModRecipeTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StewPotCookingRecipe implements Recipe<Inventory> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StewPotCookingRecipe.class);
    
    private final Identifier id;
    private final List<ItemStack> inputs;
    private final List<ItemStack> outputs;
    private final int cookTime;
    private final boolean scalable;
    
    public StewPotCookingRecipe(Identifier id, List<ItemStack> inputs, List<ItemStack> outputs, int cookTime, boolean scalable) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
        this.cookTime = cookTime;
        
        if (scalable && inputs.size() != 1) {
            LOGGER.warn("Recipe {} has scalable=true but has {} inputs. Only recipes with 1 input can be scalable. Ignoring scalable flag.", id, inputs.size());
            this.scalable = false;
        } else {
            this.scalable = scalable;
        }
    }
    
    public StewPotCookingRecipe(Identifier id, List<ItemStack> inputs, List<ItemStack> outputs, int cookTime) {
        this(id, inputs, outputs, cookTime, false);
    }
    
    private static DefaultedList<Ingredient> convertToIngredients(List<ItemStack> itemStacks) {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        for (ItemStack stack : itemStacks) {
            ItemStack ingredientStack = stack.copy();
            ingredientStack.setCount(1);
            ingredients.add(Ingredient.ofStacks(ingredientStack));
        }
        return ingredients;
    }
    
    @Override
    public boolean matches(Inventory inventory, net.minecraft.world.World world) {
        List<ItemStack> inventoryItems = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                inventoryItems.add(stack.copy());
            }
        }
        return matches(inventoryItems);
    }
    
    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).copy();
    }
    
    @Override
    public boolean fits(int width, int height) {
        return true;
    }
    
    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).copy();
    }
    
    @Override
    public Identifier getId() {
        return id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.STEW_POT_COOKING_SERIALIZER;
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.STEW_POT_COOKING_RECIPE_TYPE;
    }
    
    public net.minecraft.recipe.book.CookingRecipeCategory getCategory() {
        return ModRecipeTypes.STEW_POT_CATEGORY;
    }
    
    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return convertToIngredients(inputs);
    }
    
    public boolean matches(List<ItemStack> materials) {
        for (ItemStack inputStack : inputs) {
            int requiredCount = inputStack.getCount();
            int availableCount = 0;
            
            for (ItemStack materialStack : materials) {
                if (ItemStack.areItemsEqual(inputStack, materialStack) && (inputStack.getNbt() == null ? materialStack.getNbt() == null : inputStack.getNbt().equals(materialStack.getNbt()))) {
                    availableCount += materialStack.getCount();
                    if (availableCount >= requiredCount) {
                        break;
                    }
                }
            }
            
            if (availableCount < requiredCount) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean matchesStrictly(List<ItemStack> materials) {
        int totalInputCount = 0;
        for (ItemStack inputStack : inputs) {
            totalInputCount += inputStack.getCount();
        }
        
        int totalMaterialCount = 0;
        for (ItemStack materialStack : materials) {
            totalMaterialCount += materialStack.getCount();
        }
        
        if (totalInputCount != totalMaterialCount) {
            return false;
        }
        
        for (ItemStack inputStack : inputs) {
            int requiredCount = inputStack.getCount();
            int availableCount = 0;
            
            for (ItemStack materialStack : materials) {
                if (ItemStack.areItemsEqual(inputStack, materialStack) && (inputStack.getNbt() == null ? materialStack.getNbt() == null : inputStack.getNbt().equals(materialStack.getNbt()))) {
                    availableCount += materialStack.getCount();
                }
            }
            
            if (availableCount != requiredCount) {
                return false;
            }
        }
        
        return true;
    }
    
    public void consumeMaterials(List<ItemStack> materials, int scaleFactor) {
        for (ItemStack inputStack : inputs) {
            int requiredCount = inputStack.getCount() * scaleFactor;
            
            for (int i = 0; i < materials.size(); i++) {
                ItemStack materialStack = materials.get(i);
                if (ItemStack.areItemsEqual(inputStack, materialStack) && (inputStack.getNbt() == null ? materialStack.getNbt() == null : inputStack.getNbt().equals(materialStack.getNbt()))) {
                    if (materialStack.getCount() > requiredCount) {
                        materialStack.decrement(requiredCount);
                        requiredCount = 0;
                    } else {
                        requiredCount -= materialStack.getCount();
                        materials.remove(i);
                        i--;
                    }
                    
                    if (requiredCount == 0) {
                        break;
                    }
                }
            }
        }
    }
    
    public void consumeMaterials(List<ItemStack> materials) {
        consumeMaterials(materials, 1);
    }
    
    public int calculateScaleFactor(List<ItemStack> materials) {
        if (!scalable) {
            return 1;
        }
        
        int maxFactor = Integer.MAX_VALUE;
        for (ItemStack inputStack : inputs) {
            int requiredCount = inputStack.getCount();
            int availableCount = 0;
            
            for (ItemStack materialStack : materials) {
                if (ItemStack.areItemsEqual(inputStack, materialStack) && 
                    (inputStack.getNbt() == null ? materialStack.getNbt() == null : inputStack.getNbt().equals(materialStack.getNbt()))) {
                    availableCount += materialStack.getCount();
                }
            }
            
            if (availableCount < requiredCount) {
                return 0;
            }
            
            int factor = availableCount / requiredCount;
            if (factor < maxFactor) {
                maxFactor = factor;
            }
        }
        
        return maxFactor;
    }
    
    public List<ItemStack> getOutputs(int scaleFactor) {
        List<ItemStack> result = new ArrayList<>(outputs.size());
        for (ItemStack stack : outputs) {
            ItemStack scaled = stack.copy();
            scaled.setCount(scaled.getCount() * scaleFactor);
            result.add(scaled);
        }
        return result;
    }
    
    public List<ItemStack> getOutputs() {
        return getOutputs(1);
    }
    
    public int getCookTime() {
        return cookTime;
    }
    
    public boolean isScalable() {
        return scalable;
    }
    
    public List<ItemStack> getInputs() {
        List<ItemStack> result = new ArrayList<>(inputs.size());
        for (ItemStack stack : inputs) {
            result.add(stack.copy());
        }
        return result;
    }
    
    public static class Serializer implements RecipeSerializer<StewPotCookingRecipe> {
        @Override
        public StewPotCookingRecipe read(Identifier id, JsonObject json) {
            List<ItemStack> inputs = new ArrayList<>();
            List<ItemStack> outputs = new ArrayList<>();
            
            JsonArray inputsArray = JsonHelper.getArray(json, "inputs");
            for (JsonElement element : inputsArray) {
                JsonObject inputObject = element.getAsJsonObject();
                String itemId = JsonHelper.getString(inputObject, "item");
                int count = JsonHelper.getInt(inputObject, "count", 1);
                net.minecraft.item.Item item = Registries.ITEM.get(new Identifier(itemId));
                ItemStack stack = new ItemStack(item, count);
                
                if (inputObject.has("nbt")) {
                    try {
                        String nbtString = JsonHelper.getString(inputObject, "nbt");
                        NbtCompound nbt = StringNbtReader.parse(nbtString);
                        stack.setNbt(nbt);
                    } catch (Exception e) {
                        throw new JsonParseException("Failed to parse NBT data for input: " + e.getMessage(), e);
                    }
                }
                
                inputs.add(stack);
            }
            
            if (inputs.isEmpty()) {
                throw new JsonParseException("No inputs for stew pot cooking recipe");
            }
            
            JsonArray outputsArray = JsonHelper.getArray(json, "outputs");
            for (JsonElement element : outputsArray) {
                JsonObject outputObject = element.getAsJsonObject();
                String itemId = JsonHelper.getString(outputObject, "item");
                int count = JsonHelper.getInt(outputObject, "count", 1);
                net.minecraft.item.Item item = Registries.ITEM.get(new Identifier(itemId));
                ItemStack stack = new ItemStack(item, count);
                
                if (outputObject.has("nbt")) {
                    try {
                        String nbtString = JsonHelper.getString(outputObject, "nbt");
                        NbtCompound nbt = StringNbtReader.parse(nbtString);
                        stack.setNbt(nbt);
                    } catch (Exception e) {
                        throw new JsonParseException("Failed to parse NBT data for output: " + e.getMessage(), e);
                    }
                }
                
                outputs.add(stack);
            }
            
            if (outputs.isEmpty()) {
                throw new JsonParseException("No outputs for stew pot cooking recipe");
            }
            
            int cookTime = JsonHelper.getInt(json, "cookTime", 200);
            boolean scalable = JsonHelper.getBoolean(json, "scalable", false);
            
            return new StewPotCookingRecipe(id, inputs, outputs, cookTime, scalable);
        }
        
        @Override
        public StewPotCookingRecipe read(Identifier id, PacketByteBuf buf) {
            List<ItemStack> inputs = new ArrayList<>();
            List<ItemStack> outputs = new ArrayList<>();
            
            int inputCount = buf.readVarInt();
            for (int i = 0; i < inputCount; i++) {
                inputs.add(buf.readItemStack());
            }
            
            int outputCount = buf.readVarInt();
            for (int i = 0; i < outputCount; i++) {
                outputs.add(buf.readItemStack());
            }
            
            int cookTime = buf.readVarInt();
            boolean scalable = buf.readBoolean();
            
            return new StewPotCookingRecipe(id, inputs, outputs, cookTime, scalable);
        }
        
        @Override
        public void write(PacketByteBuf buf, StewPotCookingRecipe recipe) {
            buf.writeVarInt(recipe.inputs.size());
            for (ItemStack input : recipe.inputs) {
                buf.writeItemStack(input);
            }
            
            buf.writeVarInt(recipe.outputs.size());
            for (ItemStack output : recipe.outputs) {
                buf.writeItemStack(output);
            }
            
            buf.writeVarInt(recipe.cookTime);
            buf.writeBoolean(recipe.scalable);
        }
    }
}
