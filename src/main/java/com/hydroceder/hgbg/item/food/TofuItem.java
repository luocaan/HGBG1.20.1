package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.item.Item;

public class TofuItem extends Item {
    public TofuItem(Settings settings) {
        super(settings.food(FoodProperties.createFood(
            FoodProperties.TOFU_HUNGER,
            FoodProperties.TOFU_SATURATION
        ).build()));
    }
}
