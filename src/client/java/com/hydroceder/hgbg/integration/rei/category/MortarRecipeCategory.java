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
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class MortarRecipeCategory implements DisplayCategory<MortarRecipeDisplay> {

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.MORTAR_AND_PESTLE);
    }

    @Override
    public Text getTitle() {
        return Text.translatable("rei.hunger-begone.mortar");
    }

    @Override
    public CategoryIdentifier<? extends MortarRecipeDisplay> getCategoryIdentifier() {
        return HgbgModREI.MORTAR;
    }

    @Override
    public List<Widget> setupDisplay(MortarRecipeDisplay display, Rectangle bounds) {
        Point origin = bounds.getLocation();
        final List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));
        Rectangle bgBounds = HgbgModREI.centeredIntoRecipeBase(origin, 116, 56);

        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 1, bgBounds.y + 19))
                .entries(display.getInputEntries().get(0)).markInput());

        widgets.add(Widgets.createArrow(new Point(bgBounds.x + 30, bgBounds.y + 19)));

        widgets.add(Widgets.createSlot(new Point(bgBounds.x + 60, bgBounds.y + 19))
                .entries(display.getOutputEntries().get(0)).markOutput());

        if (display.hasContainer()) {
            Point containerSlotPos = new Point(bgBounds.x + 95, bgBounds.y + 19);
            widgets.add(Widgets.createSlot(containerSlotPos)
                    .entries(display.getContainerOutput()).markInput());
            widgets.add(Widgets.createLabel(new Point(
                    containerSlotPos.x + 20, containerSlotPos.y),
                    Text.literal("○").formatted(Formatting.GRAY))
                    .noShadow()
                    .tooltip(Text.translatable("rei.hunger-begone.mortar.container")));
        }

        return widgets;
    }

}