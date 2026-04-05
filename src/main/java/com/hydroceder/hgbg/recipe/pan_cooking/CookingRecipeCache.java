package com.hydroceder.hgbg.recipe.pan_cooking;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 烹饪配方缓存系统
 * 用于缓存所有"可作为输入且输出为食物"的物品
 */
public class CookingRecipeCache {
    private static final Set<Item> cookableFoodCache = new HashSet<>();
    private static boolean initialized = false;
    
    /**
     * 构建缓存
     * 遍历所有熔炉配方，检查输出是否为食物
     */
    public static void buildCache(MinecraftServer server) {
        cookableFoodCache.clear();
        
        DynamicRegistryManager registryManager = server.getRegistryManager();
        List<SmeltingRecipe> recipes = server.getRecipeManager().listAllOfType(RecipeType.SMELTING);
        
        for (SmeltingRecipe recipe : recipes) {
            ItemStack output = recipe.getOutput(registryManager);
            
            // 检查输出是否为食物
            if (output.getItem().isFood()) {
                Ingredient ingredient = recipe.getIngredients().get(0);
                
                // 遍历所有匹配的输入物品
                for (ItemStack matchingStack : ingredient.getMatchingStacks()) {
                    cookableFoodCache.add(matchingStack.getItem());
                }
            }
        }
        
        initialized = true;
    }
    
    /**
     * 检查物品是否可烹饪
     */
    public static boolean isCookableFood(Item item) {
        return cookableFoodCache.contains(item);
    }
    
    /**
     * 检查物品栈是否可烹饪
     */
    public static boolean isCookableFood(ItemStack stack) {
        return !stack.isEmpty() && isCookableFood(stack.getItem());
    }
    
    /**
     * 获取缓存大小（用于调试）
     */
    public static int getCacheSize() {
        return cookableFoodCache.size();
    }
    
    /**
     * 检查缓存是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * 清空缓存
     */
    public static void clear() {
        cookableFoodCache.clear();
        initialized = false;
    }
}
