package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.ColaItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;

public class BerryColaBottleItem extends ColaItem {
    public BerryColaBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.BERRY_COLA_HUNGER)
            .saturationModifier(FoodProperties.BERRY_COLA_SATURATION)
            .build()));
    }
}
