package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.item.Item;

public class RoseJamBreadItem extends Item {
    public RoseJamBreadItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.ROSE_JAM_BREAD_HUNGER,
            FoodProperties.ROSE_JAM_BREAD_SATURATION
        ).build()));
    }
}
