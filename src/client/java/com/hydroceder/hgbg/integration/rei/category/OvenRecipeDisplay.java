package com.hydroceder.hgbg.integration.rei.category;

import com.hydroceder.hgbg.integration.rei.HgbgModREI;
import com.hydroceder.hgbg.recipe.oven.OvenRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class OvenRecipeDisplay extends BasicDisplay {

    public OvenRecipeDisplay(OvenRecipe recipe) {
        super(buildInputs(recipe),
                Collections.singletonList(EntryIngredients.of(recipe.getOutput())),
                Optional.ofNullable(recipe.getId()));
    }

    private static List<EntryIngredient> buildInputs(OvenRecipe recipe) {
        List<EntryIngredient> inputs = new ArrayList<>();
        for (var ingredient : recipe.getIngredients()) {
            var stacks = ingredient.getMatchingStacks();
            if (stacks.length > 0) {
                inputs.add(EntryIngredients.ofItemStacks(List.of(stacks)));
            } else {
                inputs.add(EntryIngredients.ofIngredient(ingredient));
            }
        }
        return inputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return HgbgModREI.OVEN;
    }

}