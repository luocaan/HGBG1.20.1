package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;

public class BaconItem extends Item {
    public BaconItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.BACON_HUNGER,
            FoodProperties.BACON_SATURATION
        )
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 3600, 1), 1.0f)
            .build()));
    }
}
