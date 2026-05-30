package com.hydroceder.hgbg.integration.rei.category;

import com.hydroceder.hgbg.integration.rei.HgbgModREI;
import com.hydroceder.hgbg.recipe.mortar.MortarAndPestleRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class MortarRecipeDisplay extends BasicDisplay {

    private final EntryIngredient containerOutput;

    public MortarRecipeDisplay(MortarAndPestleRecipe recipe) {
        super(Collections.singletonList(buildInput(recipe)),
                Collections.singletonList(EntryIngredients.of(recipe.getOutput())),
                Optional.ofNullable(recipe.getId()));
        if (recipe.requiresContainer()) {
            containerOutput = EntryIngredients.of(new ItemStack(recipe.getRequiredContainer()));
        } else {
            containerOutput = null;
        }
    }

    private static EntryIngredient buildInput(MortarAndPestleRecipe recipe) {
        var stacks = recipe.getIngredient().getMatchingStacks();
        if (stacks.length > 0) {
            return EntryIngredients.ofItemStacks(List.of(stacks));
        } else {
            return EntryIngredients.ofIngredient(recipe.getIngredient());
        }
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return HgbgModREI.MORTAR;
    }

    public EntryIngredient getContainerOutput() {
        return containerOutput;
    }

    public boolean hasContainer() {
        return containerOutput != null;
    }

}