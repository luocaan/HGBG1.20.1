package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;

public class RoseJamBottleItem extends DrinkItem {
    public RoseJamBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.ROSE_JAM_HUNGER)
            .saturationModifier(FoodProperties.ROSE_JAM_SATURATION)
            .build()));
    }
}
