package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ChipsItem extends Item {
    public ChipsItem(FabricItemSettings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.CHIPS_HUNGER,
            FoodProperties.CHIPS_SATURATION
        ).build()));
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 16;
    }
}
