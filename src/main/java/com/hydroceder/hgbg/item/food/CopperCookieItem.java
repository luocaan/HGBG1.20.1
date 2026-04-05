package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.item.Item;

/**
 * 涂蜡的镶铜曲奇物品类
 * 提供3点饥饿值，4点饱和度
 */
public class CopperCookieItem extends Item {
    public CopperCookieItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.COPPER_COOKIE_HUNGER,
            FoodProperties.COPPER_COOKIE_SATURATION
        ).build()));
    }
}
