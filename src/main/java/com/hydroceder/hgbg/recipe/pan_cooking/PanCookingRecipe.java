package com.hydroceder.hgbg.recipe.pan_cooking;

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

import java.util.ArrayList;
import java.util.List;

/**
 * 锅烹饪配方类
 * 用于定义锅专属的烹饪配方
 * 支持多材料输入和多物品输出
 */
public class PanCookingRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final List<ItemStack> inputs;
    private final List<ItemStack> outputs;
    private final int cookTime; // 烹饪时间（刻）
    
    public PanCookingRecipe(Identifier id, List<ItemStack> inputs, List<ItemStack> outputs, int cookTime) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
        this.cookTime = cookTime;
    }
    
    public PanCookingRecipe(List<ItemStack> inputs, List<ItemStack> outputs, int cookTime) {
        this(new Identifier("hunger-begone", "pan_cooking"), inputs, outputs, cookTime);
    }
    
    /**
     * 将 ItemStack 列表转换为 Ingredient 列表
     */
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
        // 将 Inventory 转换为 ItemStack 列表
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
        // 返回第一个输出的副本
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
        return ModRecipeTypes.PAN_COOKING_SERIALIZER;
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.PAN_COOKING_RECIPE_TYPE;
    }
    
    public net.minecraft.recipe.book.CookingRecipeCategory getCategory() {
        return ModRecipeTypes.PAN_COOKING_CATEGORY;
    }
    
    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return convertToIngredients(inputs);
    }
    
    /**
     * 检查材料列表是否匹配此配方
     */
    public boolean matches(List<ItemStack> materials) {
        // 检查每种输入材料是否在材料列表中存在足够数量
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
    
    /**
     * 从材料列表中消耗配方所需的材料
     */
    public void consumeMaterials(List<ItemStack> materials) {
        for (ItemStack inputStack : inputs) {
            int requiredCount = inputStack.getCount();
            
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
    
    /**
     * 获取输出物品列表
     */
    public List<ItemStack> getOutputs() {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : outputs) {
            result.add(stack.copy());
        }
        return result;
    }
    
    /**
     * 获取烹饪时间
     */
    public int getCookTime() {
        return cookTime;
    }
    
    /**
     * 获取输入物品列表
     */
    public List<ItemStack> getInputs() {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : inputs) {
            result.add(stack.copy());
        }
        return result;
    }
    
    /**
     * 锅烹饪配方序列化器
     */
    public static class Serializer implements RecipeSerializer<PanCookingRecipe> {
        @Override
        public PanCookingRecipe read(Identifier id, JsonObject json) {
            List<ItemStack> inputs = new ArrayList<>();
            List<ItemStack> outputs = new ArrayList<>();
            
            // 读取输入材料
            JsonArray inputsArray = JsonHelper.getArray(json, "inputs");
            for (JsonElement element : inputsArray) {
                JsonObject inputObject = element.getAsJsonObject();
                String itemId = JsonHelper.getString(inputObject, "item");
                int count = JsonHelper.getInt(inputObject, "count", 1);
                net.minecraft.item.Item item = Registries.ITEM.get(new Identifier(itemId));
                ItemStack stack = new ItemStack(item, count);
                
                // 如果JSON中有nbt字段，读取并解析NBT数据
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
                throw new JsonParseException("No inputs for pan cooking recipe");
            }
            
            // 读取输出物品
            JsonArray outputsArray = JsonHelper.getArray(json, "outputs");
            for (JsonElement element : outputsArray) {
                JsonObject outputObject = element.getAsJsonObject();
                String itemId = JsonHelper.getString(outputObject, "item");
                int count = JsonHelper.getInt(outputObject, "count", 1);
                net.minecraft.item.Item item = Registries.ITEM.get(new Identifier(itemId));
                ItemStack stack = new ItemStack(item, count);
                
                // 如果JSON中有nbt字段，读取并解析NBT数据
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
                throw new JsonParseException("No outputs for pan cooking recipe");
            }
            
            // 读取烹饪时间
            int cookTime = JsonHelper.getInt(json, "cookTime", 200);
            
            return new PanCookingRecipe(id, inputs, outputs, cookTime);
        }
        
        @Override
        public PanCookingRecipe read(Identifier id, PacketByteBuf buf) {
            List<ItemStack> inputs = new ArrayList<>();
            List<ItemStack> outputs = new ArrayList<>();
            
            // 读取输入材料数量
            int inputCount = buf.readVarInt();
            for (int i = 0; i < inputCount; i++) {
                inputs.add(buf.readItemStack());
            }
            
            // 读取输出物品数量
            int outputCount = buf.readVarInt();
            for (int i = 0; i < outputCount; i++) {
                outputs.add(buf.readItemStack());
            }
            
            // 读取烹饪时间
            int cookTime = buf.readVarInt();
            
            return new PanCookingRecipe(id, inputs, outputs, cookTime);
        }
        
        @Override
        public void write(PacketByteBuf buf, PanCookingRecipe recipe) {
            // 写入输入材料数量
            buf.writeVarInt(recipe.inputs.size());
            for (ItemStack input : recipe.inputs) {
                buf.writeItemStack(input);
            }
            
            // 写入输出物品数量
            buf.writeVarInt(recipe.outputs.size());
            for (ItemStack output : recipe.outputs) {
                buf.writeItemStack(output);
            }
            
            // 写入烹饪时间
            buf.writeVarInt(recipe.cookTime);
        }
    }
}
