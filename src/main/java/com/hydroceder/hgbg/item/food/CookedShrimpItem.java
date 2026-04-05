package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.item.Item;

/**
 * 熟虾物品类
 * 恢复饥饿值5点，饱和度5点
 */
public class CookedShrimpItem extends Item {
    
    public CookedShrimpItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.COOKED_SHRIMP_HUNGER,
            FoodProperties.COOKED_SHRIMP_SATURATION
        ).build()));
    }
}
