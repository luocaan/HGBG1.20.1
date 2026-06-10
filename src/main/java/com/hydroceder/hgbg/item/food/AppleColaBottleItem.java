package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.ColaItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;

public class AppleColaBottleItem extends ColaItem {
    public AppleColaBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.APPLE_COLA_HUNGER)
            .saturationModifier(FoodProperties.APPLE_COLA_SATURATION)
            .build()));
    }
}
