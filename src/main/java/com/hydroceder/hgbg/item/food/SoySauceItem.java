package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SoySauceItem extends DrinkItem {
    public SoySauceItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.SOY_SAUCE_HUNGER)
            .saturationModifier(FoodProperties.SOY_SAUCE_SATURATION)
            .build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        if (!world.isClient && user instanceof PlayerEntity) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0, false, true));
        }
        
        return result;
    }
}
