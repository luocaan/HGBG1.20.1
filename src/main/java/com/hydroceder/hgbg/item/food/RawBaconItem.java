package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.item.Item;

/**
 * 生培根物品类
 * 遇到饱和度4点，饥饿值2点
 */
public class RawBaconItem extends Item {
    
    public RawBaconItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.RAW_BACON_HUNGER,
            FoodProperties.RAW_BACON_SATURATION
        ).build()));
    }
}
