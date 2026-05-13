package com.hydroceder.hgbg.recipe.pan_cooking;

import com.hydroceder.hgbg.recipe.ModRecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.recipe.RecipeManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * 锅烹饪配方管理器
 * 用于管理所有锅专属的烹饪配方
 */
public class PanCookingRecipeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PanCookingRecipeManager.class);
    private static final List<PanCookingRecipe> recipes = Collections.synchronizedList(new ArrayList<>());
    private static final Random random = new Random();
    
    /**
     * 配方匹配结果
     */
    public static class MatchResult {
        public final PanCookingRecipe recipe;
        public final int scaleFactor;
        public final List<String> seasoningIds;
        
        public MatchResult(PanCookingRecipe recipe, int scaleFactor, List<String> seasoningIds) {
            this.recipe = recipe;
            this.scaleFactor = scaleFactor;
            this.seasoningIds = seasoningIds;
        }
    }
    
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
        LOGGER.info("Loaded {} pan cooking recipes from server", count);
    }
    
    /**
     * 检查两个材料列表是否完全一致（材料类型和数量都必须匹配）
     */
    private static boolean areMaterialsExactlyMatching(List<ItemStack> recipeInputs, List<ItemStack> materials) {
        // 先创建副本，避免修改原数据
        List<ItemStack> remainingMaterials = new ArrayList<>();
        for (ItemStack stack : materials) {
            remainingMaterials.add(stack.copy());
        }
        
        // 检查每种配方输入材料
        for (ItemStack inputStack : recipeInputs) {
            int requiredCount = inputStack.getCount();
            boolean found = false;
            
            for (int i = 0; i < remainingMaterials.size(); i++) {
                ItemStack materialStack = remainingMaterials.get(i);
                if (inputStack.isOf(materialStack.getItem())) {
                    
                    if (materialStack.getCount() >= requiredCount) {
                        materialStack.decrement(requiredCount);
                        if (materialStack.isEmpty()) {
                            remainingMaterials.remove(i);
                        }
                        found = true;
                        break;
                    }
                }
            }
            
            if (!found) {
                return false;
            }
        }
        
        // 检查是否有剩余材料
        for (ItemStack stack : remainingMaterials) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 查找匹配的配方
     * 优先级：
     * 1. 完全精准匹配的不可缩放配方（材料类型和数量完全一致）
     * 2. 如果有多个完全匹配的，随机选择一个
     * 3. 如果没有完全匹配的，查找可缩放配方
     * 注意：调味料会被自动分离，不计入配方匹配
     */
    public static Optional<MatchResult> findRecipe(List<ItemStack> materials) {
        // 先分离调味料和食材
        com.hydroceder.hgbg.util.SeasoningNBT.SeparationResult separationResult = 
            com.hydroceder.hgbg.util.SeasoningNBT.separateSeasonings(materials);
        List<ItemStack> nonSeasoningMaterials = separationResult.nonSeasoningMaterials;
        List<String> seasoningIds = separationResult.seasoningIds;
        
        // 第一阶段：寻找完全精准匹配的不可缩放配方
        List<PanCookingRecipe> exactMatches = new ArrayList<>();
        
        for (PanCookingRecipe recipe : recipes) {
            if (!recipe.isScalable() && areMaterialsExactlyMatching(recipe.getInputs(), nonSeasoningMaterials)) {
                exactMatches.add(recipe);
            }
        }
        
        if (!exactMatches.isEmpty()) {
            // 随机选择一个完全匹配的配方
            PanCookingRecipe selectedRecipe = exactMatches.get(random.nextInt(exactMatches.size()));
            return Optional.of(new MatchResult(selectedRecipe, 1, seasoningIds));
        }
        
        // 第二阶段：寻找可缩放配方
        PanCookingRecipe bestScalableRecipe = null;
        int bestScaleFactor = 0;
        
        for (PanCookingRecipe recipe : recipes) {
            if (recipe.isScalable()) {
                int scaleFactor = recipe.calculateScaleFactor(nonSeasoningMaterials);
                if (scaleFactor > bestScaleFactor) {
                    bestScalableRecipe = recipe;
                    bestScaleFactor = scaleFactor;
                }
            }
        }
        
        if (bestScalableRecipe != null && bestScaleFactor > 0) {
            return Optional.of(new MatchResult(bestScalableRecipe, bestScaleFactor, seasoningIds));
        }
        
        return Optional.empty();
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
