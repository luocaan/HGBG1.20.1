package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class FishAndChipsItem extends Item {
    public FishAndChipsItem(FabricItemSettings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.FISH_AND_CHIPS_HUNGER,
            FoodProperties.FISH_AND_CHIPS_SATURATION
        ).build()));
    }
}
