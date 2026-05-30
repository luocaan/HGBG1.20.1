package com.hydroceder.hgbg.integration.rei.category;

import com.hydroceder.hgbg.block.ModBlocks;
import com.hydroceder.hgbg.integration.rei.HgbgModREI;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class OvenRecipeCategory implements DisplayCategory<OvenRecipeDisplay> {

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.OVEN);
    }

    @Override
    public Text getTitle() {
        return Text.translatable("rei.hunger-begone.oven");
    }

    @Override
    public CategoryIdentifier<? extends OvenRecipeDisplay> getCategoryIdentifier() {
        return HgbgModREI.OVEN;
    }

    @Override
    public List<Widget> setupDisplay(OvenRecipeDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        final List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));
        Rectangle bgBounds = HgbgModREI.centeredIntoRecipeBase(origin, 116, 56);

        List<EntryIngredient> ingredientEntries = display.getInputEntries();
        for (int i = 0; i < ingredientEntries.size(); i++) {
            Point slotLoc = new Point(bgBounds.x + 1 + i % 3 * 18, bgBounds.y + 1 + (i / 3) * 18);
            widgets.add(Widgets.createSlot(slotLoc).entries(ingredientEntries.get(i)).markInput());
        }

        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 95, bgBounds.y + 19))
                .entries(display.getOutputEntries().get(0)).markOutput());

        widgets.add(Widgets.createArrow(new Point(bgBounds.x + 60, bgBounds.y + 19)));

        return widgets;
    }

}