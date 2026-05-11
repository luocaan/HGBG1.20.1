package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.block.ModBlocks;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class LemonPickleItem extends BlockItem {
    public LemonPickleItem(Block block, Settings settings) {
        super(block, settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.LEMON_PICKLE_HUNGER,
            FoodProperties.LEMON_PICKLE_SATURATION
        ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        return FoodProperties.handleBowlReturn(stack, world, user, result);
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, java.util.List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.hunger-begone.placeable.tooltip").formatted(net.minecraft.util.Formatting.GRAY));
    }
}
