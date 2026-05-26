package com.hydroceder.hgbg.recipe;

import com.hydroceder.hgbg.recipe.oven.OvenRecipe;
import com.hydroceder.hgbg.recipe.mortar.MortarAndPestleRecipe;
import com.hydroceder.hgbg.recipe.pan_cooking.PanCookingRecipe;
import com.hydroceder.hgbg.recipe.stew_pot_cooking.StewPotCookingRecipe;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 模组配方类型注册类
 */
public class ModRecipeTypes {
    public static final RecipeType<OvenRecipe> OVEN_RECIPE_TYPE = new RecipeType<OvenRecipe>() {
        @Override
        public String toString() {
            return "hunger-begone:oven";
        }
    };
    
    public static final RecipeSerializer<OvenRecipe> OVEN_SERIALIZER = new OvenRecipe.Serializer();
    
    public static final CookingRecipeCategory OVEN_CATEGORY = CookingRecipeCategory.MISC;
    
    public static final RecipeType<MortarAndPestleRecipe> MORTAR_AND_PESTLE_RECIPE_TYPE = new RecipeType<MortarAndPestleRecipe>() {
        @Override
        public String toString() {
            return "hunger-begone:mortar_and_pestle";
        }
    };
    
    public static final RecipeSerializer<MortarAndPestleRecipe> MORTAR_AND_PESTLE_SERIALIZER = new MortarAndPestleRecipe.Serializer();
    
    public static final CookingRecipeCategory MORTAR_CATEGORY = CookingRecipeCategory.MISC;
    
    public static final RecipeType<PanCookingRecipe> PAN_COOKING_RECIPE_TYPE = new RecipeType<PanCookingRecipe>() {
        @Override
        public String toString() {
            return "hunger-begone:pan_cooking";
        }
    };
    
    public static final RecipeSerializer<PanCookingRecipe> PAN_COOKING_SERIALIZER = new PanCookingRecipe.Serializer();
    
    public static final CookingRecipeCategory PAN_COOKING_CATEGORY = CookingRecipeCategory.MISC;
    
    public static final RecipeType<StewPotCookingRecipe> STEW_POT_COOKING_RECIPE_TYPE = new RecipeType<StewPotCookingRecipe>() {
        @Override
        public String toString() {
            return "hunger-begone:stew_pot_cooking";
        }
    };
    
    public static final RecipeSerializer<StewPotCookingRecipe> STEW_POT_COOKING_SERIALIZER = new StewPotCookingRecipe.Serializer();
    
    public static final CookingRecipeCategory STEW_POT_CATEGORY = CookingRecipeCategory.MISC;
    
    /**
     * 注册所有配方类型
     */
    public static void register() {
        Registry.register(Registries.RECIPE_TYPE, new Identifier("hunger-begone", "oven"), OVEN_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("hunger-begone", "oven"), OVEN_SERIALIZER);
        
        Registry.register(Registries.RECIPE_TYPE, new Identifier("hunger-begone", "mortar_and_pestle"), MORTAR_AND_PESTLE_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("hunger-begone", "mortar_and_pestle"), MORTAR_AND_PESTLE_SERIALIZER);
        
        Registry.register(Registries.RECIPE_TYPE, new Identifier("hunger-begone", "pan_cooking"), PAN_COOKING_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("hunger-begone", "pan_cooking"), PAN_COOKING_SERIALIZER);
        
        Registry.register(Registries.RECIPE_TYPE, new Identifier("hunger-begone", "stew_pot_cooking"), STEW_POT_COOKING_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("hunger-begone", "stew_pot_cooking"), STEW_POT_COOKING_SERIALIZER);
    }
}
