package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FullSugarLemonadeItem extends DrinkItem {
    public FullSugarLemonadeItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.FULL_SUGAR_LEMONADE_HUNGER)
            .saturationModifier(FoodProperties.FULL_SUGAR_LEMONADE_SATURATION)
            .build()));
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        if (!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1200, 0, false, true));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 1200, 0, false, true));
        }
        
        return result;
    }
}
