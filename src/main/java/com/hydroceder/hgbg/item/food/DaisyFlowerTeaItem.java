package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;

public class DaisyFlowerTeaItem extends DrinkItem {
    public DaisyFlowerTeaItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.DAISY_FLOWER_TEA_HUNGER)
            .saturationModifier(FoodProperties.DAISY_FLOWER_TEA_SATURATION)
            .build()));
    }
}
