package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;

public class LemonItem extends Item {
    public LemonItem(FabricItemSettings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.LEMON_HUNGER,
            FoodProperties.LEMON_SATURATION
        )
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 160, 0), 1.0f)
            .build()));
    }
}
