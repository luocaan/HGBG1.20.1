package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.item.Item;

/**
 * 镶铁曲奇物品类
 * 提供4点饥饿值，5点饱和度
 */
public class IronCookieItem extends Item {
    public IronCookieItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.IRON_COOKIE_HUNGER,
            FoodProperties.IRON_COOKIE_SATURATION
        ).build()));
    }
}
