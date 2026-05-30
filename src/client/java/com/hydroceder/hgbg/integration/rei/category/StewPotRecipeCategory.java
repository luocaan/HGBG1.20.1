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
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class StewPotRecipeCategory implements DisplayCategory<StewPotRecipeDisplay> {

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.STEW_POT);
    }

    @Override
    public Text getTitle() {
        return Text.translatable("rei.hunger-begone.stew_pot_cooking");
    }

    @Override
    public CategoryIdentifier<? extends StewPotRecipeDisplay> getCategoryIdentifier() {
        return HgbgModREI.STEW_POT;
    }

    @Override
    public List<Widget> setupDisplay(StewPotRecipeDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        final List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));
        Rectangle bgBounds = HgbgModREI.centeredIntoRecipeBase(origin, 116, 56);

        List<EntryIngredient> ingredientEntries = display.getInputEntries();
        for (int i = 0; i < ingredientEntries.size(); i++) {
            Point slotLoc = new Point(bgBounds.x + 1 + i % 3 * 18, bgBounds.y + 1 + (i / 3) * 18);
            widgets.add(Widgets.createSlot(slotLoc).entries(ingredientEntries.get(i)).markInput());
        }

        List<EntryIngredient> outputEntries = display.getOutputEntries();
        for (int i = 0; i < outputEntries.size(); i++) {
            Point slotLoc = new Point(bgBounds.x + 95, bgBounds.y + 1 + i * 18);
            widgets.add(Widgets.createSlot(slotLoc).entries(outputEntries.get(i)).markOutput());
        }

        var cookArrow = Widgets.createArrow(new Point(bgBounds.x + 61, bgBounds.y + 19))
                .animationDurationTicks(display.getCookTime());
        widgets.add(cookArrow);
        widgets.add(Widgets.createLabel(new Point(
                cookArrow.getBounds().x + cookArrow.getBounds().width / 2, cookArrow.getBounds().y - 8),
                Text.literal(display.getCookTime() + " t"))
                .noShadow().centered().tooltip(Text.literal("Ticks"))
                .color(Formatting.DARK_GRAY.getColorValue(), Formatting.GRAY.getColorValue()));

        if (display.isScalable()) {
            Point scalablePos = new Point(bgBounds.x + 2, bgBounds.y + bgBounds.height - 10);
            widgets.add(Widgets.createLabel(scalablePos,
                    Text.literal("⇄").formatted(Formatting.GRAY))
                    .noShadow()
                    .tooltip(Text.translatable("rei.hunger-begone.scalable")));
        }

        return widgets;
    }

}