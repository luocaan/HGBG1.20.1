package com.hydroceder.hgbg.integration.rei;

import com.hydroceder.hgbg.block.ModBlocks;
import com.hydroceder.hgbg.integration.rei.category.MortarRecipeCategory;
import com.hydroceder.hgbg.integration.rei.category.MortarRecipeDisplay;
import com.hydroceder.hgbg.integration.rei.category.OvenRecipeCategory;
import com.hydroceder.hgbg.integration.rei.category.OvenRecipeDisplay;
import com.hydroceder.hgbg.integration.rei.category.PanCookingRecipeCategory;
import com.hydroceder.hgbg.integration.rei.category.PanCookingRecipeDisplay;
import com.hydroceder.hgbg.integration.rei.category.StewPotRecipeCategory;
import com.hydroceder.hgbg.integration.rei.category.StewPotRecipeDisplay;
import com.hydroceder.hgbg.recipe.ModRecipeTypes;
import com.hydroceder.hgbg.recipe.mortar.MortarAndPestleRecipe;
import com.hydroceder.hgbg.recipe.oven.OvenRecipe;
import com.hydroceder.hgbg.recipe.pan_cooking.PanCookingRecipe;
import com.hydroceder.hgbg.recipe.stew_pot_cooking.StewPotCookingRecipe;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class HgbgModREI implements REIClientPlugin {

    public static final CategoryIdentifier<OvenRecipeDisplay> OVEN = CategoryIdentifier.of("hunger-begone", "oven");
    public static final CategoryIdentifier<MortarRecipeDisplay> MORTAR = CategoryIdentifier.of("hunger-begone", "mortar");
    public static final CategoryIdentifier<PanCookingRecipeDisplay> PAN_COOKING = CategoryIdentifier.of("hunger-begone", "pan_cooking");
    public static final CategoryIdentifier<StewPotRecipeDisplay> STEW_POT = CategoryIdentifier.of("hunger-begone", "stew_pot_cooking");

    public static Rectangle centeredIntoRecipeBase(Point origin, int width, int height) {
        return centeredInto(new Rectangle(origin.x, origin.y, 150, 66), width, height);
    }

    public static Rectangle centeredInto(Rectangle origin, int width, int height) {
        return new Rectangle(origin.x + (origin.width - width) / 2, origin.y + (origin.height - height) / 2, width, height);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(
                new OvenRecipeCategory(),
                new MortarRecipeCategory(),
                new PanCookingRecipeCategory(),
                new StewPotRecipeCategory());
        registry.addWorkstations(OVEN, EntryStacks.of(ModBlocks.OVEN));
        registry.addWorkstations(MORTAR, EntryStacks.of(ModBlocks.MORTAR_AND_PESTLE));
        registry.addWorkstations(PAN_COOKING, EntryStacks.of(ModBlocks.STOVE));
        registry.addWorkstations(STEW_POT, EntryStacks.of(ModBlocks.STEW_POT));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(OvenRecipe.class, ModRecipeTypes.OVEN_RECIPE_TYPE, OvenRecipeDisplay::new);
        registry.registerRecipeFiller(MortarAndPestleRecipe.class, ModRecipeTypes.MORTAR_AND_PESTLE_RECIPE_TYPE, MortarRecipeDisplay::new);
        registry.registerRecipeFiller(PanCookingRecipe.class, ModRecipeTypes.PAN_COOKING_RECIPE_TYPE, PanCookingRecipeDisplay::new);
        registry.registerRecipeFiller(StewPotCookingRecipe.class, ModRecipeTypes.STEW_POT_COOKING_RECIPE_TYPE, StewPotRecipeDisplay::new);
    }

}