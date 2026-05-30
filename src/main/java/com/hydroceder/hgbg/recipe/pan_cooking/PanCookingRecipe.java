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
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
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
 * 支持tag作为输入材料（使用"tag"字段替代"item"字段）
 */
public class PanCookingRecipe implements Recipe<Inventory> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PanCookingRecipe.class);

    private final Identifier id;
    private final List<RecipeInput> inputs;
    private final List<ItemStack> outputs;
    private final int cookTime;
    private final boolean scalable;

    public PanCookingRecipe(Identifier id, List<RecipeInput> inputs, List<ItemStack> outputs, int cookTime, boolean scalable) {
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

    public PanCookingRecipe(Identifier id, List<RecipeInput> inputs, List<ItemStack> outputs, int cookTime) {
        this(id, inputs, outputs, cookTime, false);
    }

    /**
     * 配方输入项，可以是具体物品或tag
     */
    public static class RecipeInput {
        private final ItemStack itemStack;
        private final TagKey<net.minecraft.item.Item> tag;
        private final int count;

        public RecipeInput(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.tag = null;
            this.count = itemStack.getCount();
        }

        public RecipeInput(TagKey<net.minecraft.item.Item> tag, int count) {
            this.itemStack = null;
            this.tag = tag;
            this.count = count;
        }

        public boolean isTag() {
            return tag != null;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public TagKey<net.minecraft.item.Item> getTag() {
            return tag;
        }

        public int getCount() {
            return count;
        }

        /**
         * 检查给定的物品栈是否匹配此输入项
         */
        public boolean matches(ItemStack stack) {
            if (isTag()) {
                return stack.isIn(tag);
            } else {
                return ItemStack.areItemsEqual(itemStack, stack) &&
                    (itemStack.getNbt() == null ? stack.getNbt() == null : itemStack.getNbt().equals(stack.getNbt()));
            }
        }
    }
    
    /**
     * 将 RecipeInput 列表转换为 Ingredient 列表
     */
    private static DefaultedList<Ingredient> convertToIngredients(List<RecipeInput> recipeInputs) {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        for (RecipeInput input : recipeInputs) {
            if (input.isTag()) {
                ingredients.add(Ingredient.fromTag(input.getTag()));
            } else {
                ItemStack ingredientStack = input.getItemStack().copy();
                ingredientStack.setCount(1);
                ingredients.add(Ingredient.ofStacks(ingredientStack));
            }
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
        for (RecipeInput input : inputs) {
            int requiredCount = input.getCount();
            int availableCount = 0;

            for (ItemStack materialStack : materials) {
                if (input.matches(materialStack)) {
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
        int totalInputCount = 0;
        for (RecipeInput input : inputs) {
            totalInputCount += input.getCount();
        }

        int totalMaterialCount = 0;
        for (ItemStack materialStack : materials) {
            totalMaterialCount += materialStack.getCount();
        }

        if (totalInputCount != totalMaterialCount) {
            return false;
        }

        for (RecipeInput input : inputs) {
            int requiredCount = input.getCount();
            int availableCount = 0;

            for (ItemStack materialStack : materials) {
                if (input.matches(materialStack)) {
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
        for (RecipeInput input : inputs) {
            int requiredCount = input.getCount() * scaleFactor;

            for (int i = 0; i < materials.size(); i++) {
                ItemStack materialStack = materials.get(i);
                if (input.matches(materialStack)) {
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
     */
    public int calculateScaleFactor(List<ItemStack> materials) {
        if (!scalable) {
            return 1;
        }

        int maxFactor = Integer.MAX_VALUE;
        for (RecipeInput input : inputs) {
            int requiredCount = input.getCount();
            int availableCount = 0;

            for (ItemStack materialStack : materials) {
                if (input.matches(materialStack)) {
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
     * 获取输入列表
     */
    public List<RecipeInput> getInputs() {
        return new ArrayList<>(inputs);
    }

    /**
     * 获取输入物品栈列表（用于显示等用途，tag输入返回空栈）
     */
    public List<ItemStack> getInputStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (RecipeInput input : inputs) {
            if (input.isTag()) {
                stacks.add(ItemStack.EMPTY);
            } else {
                stacks.add(input.getItemStack());
            }
        }
        return stacks;
    }

    /**
     * 锅烹饪配方序列化器
     */
    public static class Serializer implements RecipeSerializer<PanCookingRecipe> {
        @Override
        public PanCookingRecipe read(Identifier id, JsonObject json) {
            List<RecipeInput> inputs = new ArrayList<>();
            List<ItemStack> outputs = new ArrayList<>();

            JsonArray inputsArray = JsonHelper.getArray(json, "inputs");
            for (JsonElement element : inputsArray) {
                JsonObject inputObject = element.getAsJsonObject();
                int count = JsonHelper.getInt(inputObject, "count", 1);

                if (inputObject.has("tag")) {
                    String tagId = JsonHelper.getString(inputObject, "tag");
                    TagKey<net.minecraft.item.Item> tag = TagKey.of(Registries.ITEM.getKey(), new Identifier(tagId));
                    inputs.add(new RecipeInput(tag, count));
                } else if (inputObject.has("item")) {
                    String itemId = JsonHelper.getString(inputObject, "item");
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

                    inputs.add(new RecipeInput(stack));
                } else {
                    throw new JsonParseException("Input must have either 'item' or 'tag' field");
                }
            }

            if (inputs.isEmpty()) {
                throw new JsonParseException("No inputs for pan cooking recipe");
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
                throw new JsonParseException("No outputs for pan cooking recipe");
            }

            int cookTime = JsonHelper.getInt(json, "cookTime", 200);
            boolean scalable = JsonHelper.getBoolean(json, "scalable", false);

            return new PanCookingRecipe(id, inputs, outputs, cookTime, scalable);
        }

        @Override
        public PanCookingRecipe read(Identifier id, PacketByteBuf buf) {
            List<RecipeInput> inputs = new ArrayList<>();
            List<ItemStack> outputs = new ArrayList<>();

            int inputCount = buf.readVarInt();
            for (int i = 0; i < inputCount; i++) {
                boolean isTag = buf.readBoolean();
                if (isTag) {
                    Identifier tagId = buf.readIdentifier();
                    TagKey<net.minecraft.item.Item> tag = TagKey.of(Registries.ITEM.getKey(), tagId);
                    int count = buf.readVarInt();
                    inputs.add(new RecipeInput(tag, count));
                } else {
                    ItemStack stack = buf.readItemStack();
                    inputs.add(new RecipeInput(stack));
                }
            }

            int outputCount = buf.readVarInt();
            for (int i = 0; i < outputCount; i++) {
                outputs.add(buf.readItemStack());
            }

            int cookTime = buf.readVarInt();
            boolean scalable = buf.readBoolean();

            return new PanCookingRecipe(id, inputs, outputs, cookTime, scalable);
        }

        @Override
        public void write(PacketByteBuf buf, PanCookingRecipe recipe) {
            buf.writeVarInt(recipe.inputs.size());
            for (RecipeInput input : recipe.inputs) {
                buf.writeBoolean(input.isTag());
                if (input.isTag()) {
                    buf.writeIdentifier(input.getTag().id());
                    buf.writeVarInt(input.getCount());
                } else {
                    buf.writeItemStack(input.getItemStack());
                }
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
