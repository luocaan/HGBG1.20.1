package com.hydroceder.hgbg.recipe.mortar;

import com.hydroceder.hgbg.recipe.ModRecipeTypes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import java.util.List;

public class MortarAndPestleRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final Ingredient ingredient;
    private final ItemStack output;
    private final Item requiredContainer;

    public MortarAndPestleRecipe(Identifier id, Ingredient ingredient, ItemStack output, Item requiredContainer) {
        this.id = id;
        this.ingredient = ingredient;
        this.output = output;
        this.requiredContainer = requiredContainer;
    }
    
    @Override
    public boolean matches(Inventory inventory, net.minecraft.world.World world) {
        if (inventory.size() == 0) return false;
        
        ItemStack stack = inventory.getStack(0);
        return !stack.isEmpty() && ingredient.test(stack);
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
        return ModRecipeTypes.MORTAR_AND_PESTLE_SERIALIZER;
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.MORTAR_AND_PESTLE_RECIPE_TYPE;
    }
    
    public net.minecraft.recipe.book.CookingRecipeCategory getCategory() {
        return ModRecipeTypes.MORTAR_CATEGORY;
    }
    
    public Ingredient getIngredient() {
        return ingredient;
    }

    public Item getRequiredContainer() {
        return requiredContainer;
    }

    public boolean requiresContainer() {
        return requiredContainer != null;
    }
    
    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        ingredients.add(ingredient);
        return ingredients;
    }
    
    public List<ItemStack> getInputs() {
        List<ItemStack> inputs = new ArrayList<>();
        ItemStack[] matchingStacks = ingredient.getMatchingStacks();
        if (matchingStacks.length > 0) {
            ItemStack stack = matchingStacks[0].copy();
            stack.setCount(1);
            inputs.add(stack);
        }
        return inputs;
    }
    
    /**
     * 研钵和杵配方序列化器
     */
    public static class Serializer implements RecipeSerializer<MortarAndPestleRecipe> {
        @Override
        public MortarAndPestleRecipe read(Identifier id, JsonObject json) {
            JsonObject ingredientObject = JsonHelper.getObject(json, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(ingredientObject);

            JsonObject resultObject = JsonHelper.getObject(json, "result");
            String itemId = JsonHelper.getString(resultObject, "item");
            int count = JsonHelper.getInt(resultObject, "count", 1);
            net.minecraft.item.Item item = Registries.ITEM.get(new Identifier(itemId));
            ItemStack output = new ItemStack(item, count);

            Item requiredContainer = null;
            if (json.has("required_container")) {
                String containerId = JsonHelper.getString(json, "required_container");
                requiredContainer = Registries.ITEM.get(new Identifier(containerId));
            }

            return new MortarAndPestleRecipe(id, ingredient, output, requiredContainer);
        }

        @Override
        public MortarAndPestleRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient ingredient = Ingredient.fromPacket(buf);
            ItemStack output = buf.readItemStack();
            boolean hasContainer = buf.readBoolean();
            Item requiredContainer = null;
            if (hasContainer) {
                requiredContainer = Registries.ITEM.get(buf.readIdentifier());
            }
            return new MortarAndPestleRecipe(id, ingredient, output, requiredContainer);
        }

        @Override
        public void write(PacketByteBuf buf, MortarAndPestleRecipe recipe) {
            recipe.ingredient.write(buf);
            buf.writeItemStack(recipe.output);
            boolean hasContainer = recipe.requiredContainer != null;
            buf.writeBoolean(hasContainer);
            if (hasContainer) {
                buf.writeIdentifier(Registries.ITEM.getId(recipe.requiredContainer));
            }
        }
    }
}