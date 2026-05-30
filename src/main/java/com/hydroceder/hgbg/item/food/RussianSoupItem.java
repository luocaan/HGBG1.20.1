package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RussianSoupItem extends Item {
    private static final int BUFF_DURATION = 600;

    public RussianSoupItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.RUSSIAN_SOUP_HUNGER,
            FoodProperties.RUSSIAN_SOUP_SATURATION
        )
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, BUFF_DURATION, 0), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, BUFF_DURATION, 0), 1.0f)
            .build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        return FoodProperties.handleBowlReturn(stack, world, user, result);
    }
}
