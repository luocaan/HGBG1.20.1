package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class LemonWaterItem extends DrinkItem {
    public LemonWaterItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.LEMON_WATER_HUNGER)
            .saturationModifier(FoodProperties.LEMON_WATER_SATURATION)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0), 1.0f)
            .build()));
    }
}
