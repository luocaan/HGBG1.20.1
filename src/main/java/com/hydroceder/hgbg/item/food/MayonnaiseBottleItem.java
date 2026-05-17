package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class MayonnaiseBottleItem extends DrinkItem {
    public MayonnaiseBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.MAYONNAISE_HUNGER)
            .saturationModifier(FoodProperties.MAYONNAISE_SATURATION)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 160, 0), 1.0f)
            .build()));
    }
}
