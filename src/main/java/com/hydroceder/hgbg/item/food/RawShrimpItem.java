package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.item.Item;

/**
 * 生虾物品类
 * 恢复饥饿值2点，饱和度1点
 */
public class RawShrimpItem extends Item {
    
    public RawShrimpItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.RAW_SHRIMP_HUNGER,
            FoodProperties.RAW_SHRIMP_SATURATION
        ).build()));
    }
}
