package com.hydroceder.hgbg.recipe.stew_pot_cooking;

import com.hydroceder.hgbg.recipe.ModRecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class StewPotRecipeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(StewPotRecipeManager.class);
    private static final List<StewPotCookingRecipe> recipes = Collections.synchronizedList(new ArrayList<>());
    private static final Random random = new Random();
    
    public static class MatchResult {
        public final StewPotCookingRecipe recipe;
        public final int scaleFactor;
        public final List<String> seasoningIds;
        
        public MatchResult(StewPotCookingRecipe recipe, int scaleFactor, List<String> seasoningIds) {
            this.recipe = recipe;
            this.scaleFactor = scaleFactor;
            this.seasoningIds = seasoningIds;
        }
    }
    
    public static void register(StewPotCookingRecipe recipe) {
        recipes.add(recipe);
    }
    
    public static void loadRecipesFromServer(MinecraftServer server) {
        clear();
        
        RecipeManager recipeManager = server.getRecipeManager();
        int count = 0;
        for (var recipe : recipeManager.listAllOfType(ModRecipeTypes.STEW_POT_COOKING_RECIPE_TYPE)) {
            if (recipe instanceof StewPotCookingRecipe) {
                recipes.add((StewPotCookingRecipe) recipe);
                count++;
            }
        }
        LOGGER.info("Loaded {} stew pot cooking recipes from server", count);
    }
    
    public static Optional<MatchResult> findRecipe(List<ItemStack> materials) {
        com.hydroceder.hgbg.util.SeasoningNBT.SeparationResult separationResult = 
            com.hydroceder.hgbg.util.SeasoningNBT.separateSeasonings(materials);
        List<ItemStack> nonSeasoningMaterials = separationResult.nonSeasoningMaterials;
        List<String> seasoningIds = separationResult.seasoningIds;
        
        List<StewPotCookingRecipe> exactMatches = new ArrayList<>();
        
        for (StewPotCookingRecipe recipe : recipes) {
            if (!recipe.isScalable() && recipe.matchesStrictly(nonSeasoningMaterials)) {
                exactMatches.add(recipe);
            }
        }
        
        if (!exactMatches.isEmpty()) {
            StewPotCookingRecipe selectedRecipe = exactMatches.get(random.nextInt(exactMatches.size()));
            return Optional.of(new MatchResult(selectedRecipe, 1, seasoningIds));
        }
        
        StewPotCookingRecipe bestScalableRecipe = null;
        int bestScaleFactor = 0;
        
        for (StewPotCookingRecipe recipe : recipes) {
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
    
    public static List<StewPotCookingRecipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }
    
    public static void clear() {
        recipes.clear();
    }
    
    public static Optional<StewPotCookingRecipe> getRecipeById(Identifier id) {
        for (StewPotCookingRecipe recipe : recipes) {
            if (recipe.getId().equals(id)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }
}
