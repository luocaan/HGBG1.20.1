package com.hydroceder.hgbg.integration.rei.category;

import com.hydroceder.hgbg.integration.rei.HgbgModREI;
import com.hydroceder.hgbg.recipe.pan_cooking.PanCookingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class PanCookingRecipeDisplay extends BasicDisplay {

    private final int cookTime;
    private final boolean scalable;

    public PanCookingRecipeDisplay(PanCookingRecipe recipe) {
        super(buildInputs(recipe),
                buildOutputs(recipe),
                Optional.ofNullable(recipe.getId()));
        cookTime = recipe.getCookTime();
        scalable = recipe.isScalable();
    }

    private static List<EntryIngredient> buildInputs(PanCookingRecipe recipe) {
        List<EntryIngredient> inputs = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            var stacks = ingredient.getMatchingStacks();
            if (stacks.length > 0) {
                inputs.add(EntryIngredients.ofItemStacks(List.of(stacks)));
            } else {
                inputs.add(EntryIngredients.ofIngredient(ingredient));
            }
        }
        return inputs;
    }

    private static List<EntryIngredient> buildOutputs(PanCookingRecipe recipe) {
        List<EntryIngredient> outputs = new ArrayList<>();
        for (var stack : recipe.getOutputs()) {
            outputs.add(EntryIngredients.of(stack));
        }
        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return HgbgModREI.PAN_COOKING;
    }

    public int getCookTime() {
        return cookTime;
    }

    public boolean isScalable() {
        return scalable;
    }

}