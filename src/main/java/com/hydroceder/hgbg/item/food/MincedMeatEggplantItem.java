package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;

public class MincedMeatEggplantItem extends Item {
    private static final int REGENERATION_DURATION = 100;

    public MincedMeatEggplantItem(Settings settings) {
        super(settings.food(FoodProperties.createFood(
            FoodProperties.MINCED_MEAT_EGGPLANT_HUNGER,
            FoodProperties.MINCED_MEAT_EGGPLANT_SATURATION
        )
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, REGENERATION_DURATION, 1), 1.0f)
            .build()));
    }
}
