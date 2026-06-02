package com.hydroceder.hgbg.item.food;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class PlacableBlockItem extends BlockItem {
    private final String extraTooltipKey;

    public PlacableBlockItem(Block block, Settings settings) {
        this(block, settings, null);
    }

    public PlacableBlockItem(Block block, Settings settings, String extraTooltipKey) {
        super(block, settings);
        this.extraTooltipKey = extraTooltipKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, java.util.List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.hunger-begone.placable").formatted(Formatting.GRAY));
        if (extraTooltipKey != null) {
            tooltip.add(Text.translatable(extraTooltipKey).formatted(Formatting.GRAY));
        }
    }
}
