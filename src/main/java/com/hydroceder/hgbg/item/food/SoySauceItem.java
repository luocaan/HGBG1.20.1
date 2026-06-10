package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class SoySauceItem extends DrinkItem {
    public SoySauceItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.SOY_SAUCE_HUNGER)
            .saturationModifier(FoodProperties.SOY_SAUCE_SATURATION)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0), 1.0f)
            .build()));
    }
}
