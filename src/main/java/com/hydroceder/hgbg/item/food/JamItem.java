package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;

public class JamItem extends DrinkItem {
    public JamItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.JAM_HUNGER)
            .saturationModifier(FoodProperties.JAM_SATURATION)
            .build()));
    }
}
