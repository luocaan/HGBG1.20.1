package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.item.Item;

public class ChickenWingItem extends Item {

    public ChickenWingItem(Settings settings) {
        super(settings.food(FoodProperties.createFood(
            FoodProperties.CHICKEN_WING_HUNGER,
            FoodProperties.CHICKEN_WING_SATURATION
        ).build()));
    }
}