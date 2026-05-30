package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GlowLichenMeatSoupItem extends Item {
    public GlowLichenMeatSoupItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.GLOW_LICHEN_MEAT_SOUP_HUNGER,
            FoodProperties.GLOW_LICHEN_MEAT_SOUP_SATURATION
        ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        return FoodProperties.handleBowlReturn(stack, world, user, result);
    }
}
