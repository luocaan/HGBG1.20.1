package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ShinyBerryJamBottleItem extends DrinkItem {
    public ShinyBerryJamBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.SHINY_BERRY_JAM_HUNGER)
            .saturationModifier(FoodProperties.SHINY_BERRY_JAM_SATURATION)
            .statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 1200, 0), 1.0f)
            .build()));
    }
}
