package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.item.Item;

public class JamBreadFoodItem extends Item {
    public JamBreadFoodItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.JAM_BREAD_FOOD_HUNGER,
            FoodProperties.JAM_BREAD_FOOD_SATURATION
        ).build()));
    }
}
