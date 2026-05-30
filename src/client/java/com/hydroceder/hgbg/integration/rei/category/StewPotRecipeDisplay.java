package com.hydroceder.hgbg.integration.rei.category;

import com.hydroceder.hgbg.integration.rei.HgbgModREI;
import com.hydroceder.hgbg.recipe.stew_pot_cooking.StewPotCookingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class StewPotRecipeDisplay extends BasicDisplay {

    private final int cookTime;
    private final boolean scalable;

    public StewPotRecipeDisplay(StewPotCookingRecipe recipe) {
        super(buildInputs(recipe),
                buildOutputs(recipe),
                Optional.ofNullable(recipe.getId()));
        cookTime = recipe.getCookTime();
        scalable = recipe.isScalable();
    }

    private static List<EntryIngredient> buildInputs(StewPotCookingRecipe recipe) {
        List<EntryIngredient> inputs = new ArrayList<>();
        List<ItemStack> rawInputs = recipe.getInputs();
        for (int i = 0; i < rawInputs.size(); i++) {
            if (recipe.hasTag(i)) {
                TagKey<net.minecraft.item.Item> tag = recipe.getInputTag(i);
                Ingredient tagIngredient = Ingredient.fromTag(tag);
                var stacks = tagIngredient.getMatchingStacks();
                if (stacks.length > 0) {
                    inputs.add(EntryIngredients.ofItemStacks(List.of(stacks)));
                } else {
                    inputs.add(EntryIngredients.ofIngredient(tagIngredient));
                }
            } else {
                ItemStack stack = rawInputs.get(i).copy();
                stack.setCount(1);
                inputs.add(EntryIngredients.of(stack));
            }
        }
        return inputs;
    }

    private static List<EntryIngredient> buildOutputs(StewPotCookingRecipe recipe) {
        List<EntryIngredient> outputs = new ArrayList<>();
        for (var stack : recipe.getOutputs()) {
            outputs.add(EntryIngredients.of(stack));
        }
        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return HgbgModREI.STEW_POT;
    }

    public int getCookTime() {
        return cookTime;
    }

    public boolean isScalable() {
        return scalable;
    }

}