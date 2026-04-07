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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 锅烹饪配方类
 * 用于定义锅专属的烹饪配方
 * 支持多材料输入和多物品输出
 * 支持可缩放配方（scalable=true：1:1 基础配方，自动缩放数量）
 */
public class PanCookingRecipe implements Recipe<Inventory> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PanCookingRecipe.class);
    
    private final Identifier id;
    private final List<ItemStack> inputs;
    private final List<ItemStack> outputs;
    private final int cookTime; // 烹饪时间（刻）
    private final boolean scalable; // 是否支持数量缩放
    
    public PanCookingRecipe(Identifier id, List<ItemStack> inputs, List<ItemStack> outputs, int cookTime, boolean scalable) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
        this.cookTime = cookTime;
        
        // 验证：只有单个输入的配方才能设置 scalable=true
        if (scalable && inputs.size() != 1) {
            LOGGER.warn("Recipe {} has scalable=true but has {} inputs. Only recipes with 1 input can be scalable. Ignoring scalable flag.", id, inputs.size());
            this.scalable = false;
        } else {
            this.scalable = scalable;
        }
    }
    
    public PanCookingRecipe(Identifier id, List<ItemStack> inputs, List<ItemStack> outputs, int cookTime) {
        this(id, inputs, outputs, cookTime, false);
    }
    
    public PanCookingRecipe(List<ItemStack> inputs, List<ItemStack> outputs, int cookTime) {
        this(new Identifier("hunger-begone", "pan_cooking"), inputs, outputs, cookTime, false);
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
     * 检查材料列表是否匹配此配方（宽松匹配：只要材料足够即可）
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
     * 检查材料列表是否严格匹配此配方（材料数量必须正好匹配）
     */
    public boolean matchesStrictly(List<ItemStack> materials) {
        // 计算每种材料的总数量
        int totalInputCount = 0;
        for (ItemStack inputStack : inputs) {
            totalInputCount += inputStack.getCount();
        }
        
        int totalMaterialCount = 0;
        for (ItemStack materialStack : materials) {
            totalMaterialCount += materialStack.getCount();
        }
        
        // 首先检查总数量是否一致
        if (totalInputCount != totalMaterialCount) {
            return false;
        }
        
        // 然后检查每种输入材料是否正好匹配
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
    
    /**
     * 从材料列表中消耗配方所需的材料（带缩放）
     */
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
    
    /**
     * 从材料列表中消耗配方所需的材料（默认不缩放）
     */
    public void consumeMaterials(List<ItemStack> materials) {
        consumeMaterials(materials, 1);
    }
    
    /**
     * 计算配方可以缩放的倍数
     * 对于可缩放配方，返回最大的倍数
     * 对于不可缩放配方，返回 1
     */
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
                return 0; // 材料不足
            }
            
            int factor = availableCount / requiredCount;
            if (factor < maxFactor) {
                maxFactor = factor;
            }
        }
        
        return maxFactor;
    }
    
    /**
     * 获取输出物品列表（带缩放）
     */
    public List<ItemStack> getOutputs(int scaleFactor) {
        List<ItemStack> result = new ArrayList<>(outputs.size());
        for (ItemStack stack : outputs) {
            ItemStack scaled = stack.copy();
            scaled.setCount(scaled.getCount() * scaleFactor);
            result.add(scaled);
        }
        return result;
    }
    
    /**
     * 获取输出物品列表（默认不缩放）
     */
    public List<ItemStack> getOutputs() {
        return getOutputs(1);
    }
    
    /**
     * 获取烹饪时间
     */
    public int getCookTime() {
        return cookTime;
    }
    
    /**
     * 是否支持数量缩放
     */
    public boolean isScalable() {
        return scalable;
    }
    
    /**
     * 获取输入物品列表
     */
    public List<ItemStack> getInputs() {
        List<ItemStack> result = new ArrayList<>(inputs.size());
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
            
            // 读取是否支持缩放（默认 false）
            boolean scalable = JsonHelper.getBoolean(json, "scalable", false);
            
            return new PanCookingRecipe(id, inputs, outputs, cookTime, scalable);
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
            
            // 读取是否支持缩放
            boolean scalable = buf.readBoolean();
            
            return new PanCookingRecipe(id, inputs, outputs, cookTime, scalable);
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
            
            // 写入是否支持缩放
            buf.writeBoolean(recipe.scalable);
        }
    }
}
