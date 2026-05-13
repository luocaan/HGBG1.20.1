package com.hydroceder.hgbg.item.food;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class StinkyTofuItem extends BlockItem {
    public StinkyTofuItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, java.util.List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.hunger-begone.stinky_tofu.tooltip").formatted(Formatting.GRAY));
    }
}
