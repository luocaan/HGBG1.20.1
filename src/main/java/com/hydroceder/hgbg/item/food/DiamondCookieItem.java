package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;

/**
 * 镶钻曲奇物品类
 * 提供8点饥饿值，16点饱和度
 * 食用后获得4分钟生命恢复II和4分钟抗性提升II
 */
public class DiamondCookieItem extends Item {
    public DiamondCookieItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.DIAMOND_COOKIE_HUNGER,
            FoodProperties.DIAMOND_COOKIE_SATURATION
        )
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 4800, 1), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 4800, 1), 1.0f)
            .build()));
    }
}
