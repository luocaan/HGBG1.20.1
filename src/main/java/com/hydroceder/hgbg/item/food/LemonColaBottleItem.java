package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.ColaItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;

public class LemonColaBottleItem extends ColaItem {
    public LemonColaBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.LEMON_COLA_HUNGER)
            .saturationModifier(FoodProperties.LEMON_COLA_SATURATION)
            .build()));
    }
}
