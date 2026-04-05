package com.hydroceder.hgbg.recipe.oven;

import com.hydroceder.hgbg.recipe.ModRecipeTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 烤箱配方类
 */
public class OvenRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final DefaultedList<Ingredient> ingredients;
    private final ItemStack output;
    
    public OvenRecipe(Identifier id, DefaultedList<Ingredient> ingredients, ItemStack output) {
        this.id = id;
        this.ingredients = ingredients;
        this.output = output;
    }
    
    @Override
    public boolean matches(Inventory inventory, net.minecraft.world.World world) {
        List<Ingredient> remainingIngredients = new ArrayList<>(ingredients);
        
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            
            boolean found = false;
            Iterator<Ingredient> iterator = remainingIngredients.iterator();
            while (iterator.hasNext()) {
                Ingredient ingredient = iterator.next();
                if (ingredient.test(stack)) {
                    iterator.remove();
                    found = true;
                    break;
                }
            }
            
            if (!found) return false;
        }
        
        return remainingIngredients.isEmpty();
    }
    
    /**
     * 检查物品列表是否匹配配方（顺序无关）
     */
    public boolean matches(List<ItemStack> inputItems) {
        List<Ingredient> remainingIngredients = new ArrayList<>(ingredients);
        
        for (ItemStack stack : inputItems) {
            if (stack.isEmpty()) continue;
            
            boolean found = false;
            Iterator<Ingredient> iterator = remainingIngredients.iterator();
            while (iterator.hasNext()) {
                Ingredient ingredient = iterator.next();
                if (ingredient.test(stack)) {
                    iterator.remove();
                    found = true;
                    break;
                }
            }
            
            if (!found) return false;
        }
        
        return remainingIngredients.isEmpty();
    }
    
    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }
    
    @Override
    public boolean fits(int width, int height) {
        return true;
    }
    
    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output.copy();
    }
    
    public ItemStack getOutput() {
        return output.copy();
    }
    
    @Override
    public Identifier getId() {
        return id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.OVEN_SERIALIZER;
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.OVEN_RECIPE_TYPE;
    }
    
    public net.minecraft.recipe.book.CookingRecipeCategory getCategory() {
        return ModRecipeTypes.OVEN_CATEGORY;
    }
    
    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> result = DefaultedList.of();
        for (Ingredient ingredient : ingredients) {
            result.add(ingredient);
        }
        return result;
    }
    
    public List<ItemStack> getInputs() {
        List<ItemStack> inputs = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            ItemStack[] matchingStacks = ingredient.getMatchingStacks();
            if (matchingStacks.length > 0) {
                ItemStack stack = matchingStacks[0].copy();
                stack.setCount(1);
                inputs.add(stack);
            }
        }
        return inputs;
    }
    
    /**
     * 烤箱配方序列化器
     */
    public static class Serializer implements RecipeSerializer<OvenRecipe> {
        @Override
        public OvenRecipe read(Identifier id, JsonObject json) {
            DefaultedList<Ingredient> ingredients = DefaultedList.of();
            
            JsonArray ingredientsArray = JsonHelper.getArray(json, "ingredients");
            for (JsonElement element : ingredientsArray) {
                ingredients.add(Ingredient.fromJson(element));
            }
            
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for oven recipe");
            }
            
            JsonObject resultObject = JsonHelper.getObject(json, "result");
            String itemId = JsonHelper.getString(resultObject, "item");
            int count = JsonHelper.getInt(resultObject, "count", 1);
            net.minecraft.item.Item item = Registries.ITEM.get(new Identifier(itemId));
            ItemStack output = new ItemStack(item, count);
            
            return new OvenRecipe(id, ingredients, output);
        }
        
        @Override
        public OvenRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> ingredients = DefaultedList.of();
            int ingredientCount = buf.readVarInt();
            
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.add(Ingredient.fromPacket(buf));
            }
            
            ItemStack output = buf.readItemStack();
            return new OvenRecipe(id, ingredients, output);
        }
        
        @Override
        public void write(PacketByteBuf buf, OvenRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.write(buf);
            }
            
            buf.writeItemStack(recipe.output);
        }
    }
}
