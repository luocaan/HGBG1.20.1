package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;

public class FlowerTeaItem extends DrinkItem {
    public FlowerTeaItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.FLOWER_TEA_HUNGER)
            .saturationModifier(FoodProperties.FLOWER_TEA_SATURATION)
            .build()));
    }
}
