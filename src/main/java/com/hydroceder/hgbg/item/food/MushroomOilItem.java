package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class MushroomOilItem extends DrinkItem {
    public MushroomOilItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.MUSHROOM_OIL_HUNGER)
            .saturationModifier(FoodProperties.MUSHROOM_OIL_SATURATION)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 160, 0), 1.0f)
            .build()));
    }
}
