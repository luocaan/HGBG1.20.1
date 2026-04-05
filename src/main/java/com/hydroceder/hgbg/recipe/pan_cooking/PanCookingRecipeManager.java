package com.hydroceder.hgbg.recipe.pan_cooking;

import com.hydroceder.hgbg.recipe.ModRecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.recipe.RecipeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 锅烹饪配方管理器
 * 用于管理所有锅专属的烹饪配方
 */
public class PanCookingRecipeManager {
    private static final List<PanCookingRecipe> recipes = Collections.synchronizedList(new ArrayList<>());
    
    /**
     * 注册配方
     */
    public static void register(PanCookingRecipe recipe) {
        recipes.add(recipe);
    }
    
    /**
     * 从服务器配方管理器加载JSON配方
     */
    public static void loadRecipesFromServer(MinecraftServer server) {
        clear();
        
        RecipeManager recipeManager = server.getRecipeManager();
        int count = 0;
        for (var recipe : recipeManager.listAllOfType(ModRecipeTypes.PAN_COOKING_RECIPE_TYPE)) {
            if (recipe instanceof PanCookingRecipe) {
                recipes.add((PanCookingRecipe) recipe);
                count++;
            }
        }
        System.out.println("Loaded " + count + " pan cooking recipes from server");
    }
    
    /**
     * 计算配方所需的材料总数
     */
    private static int calculateTotalInputCount(PanCookingRecipe recipe) {
        int total = 0;
        for (ItemStack input : recipe.getInputs()) {
            total += input.getCount();
        }
        return total;
    }
    
    /**
     * 查找匹配的配方
     * 优先匹配需要材料总数最多的配方
     */
    public static Optional<PanCookingRecipe> findRecipe(List<ItemStack> materials) {
        PanCookingRecipe bestMatch = null;
        int bestMatchCount = -1;
        
        for (PanCookingRecipe recipe : recipes) {
            if (recipe.matches(materials)) {
                int totalCount = calculateTotalInputCount(recipe);
                // 优先选择需要材料总数最多的配方
                if (totalCount > bestMatchCount) {
                    bestMatch = recipe;
                    bestMatchCount = totalCount;
                }
            }
        }
        
        return bestMatch != null ? Optional.of(bestMatch) : Optional.empty();
    }
    
    /**
     * 获取所有配方（用于调试）
     */
    public static List<PanCookingRecipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }
    
    /**
     * 清空所有配方（用于重载）
     */
    public static void clear() {
        recipes.clear();
    }
}
