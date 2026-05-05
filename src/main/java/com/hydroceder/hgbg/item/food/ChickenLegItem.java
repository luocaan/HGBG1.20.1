package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.item.Item;

public class ChickenLegItem extends Item {

    public ChickenLegItem(Settings settings) {
        super(settings.food(FoodProperties.createFood(
            FoodProperties.CHICKEN_LEG_HUNGER,
            FoodProperties.CHICKEN_LEG_SATURATION
        ).build()));
    }
}