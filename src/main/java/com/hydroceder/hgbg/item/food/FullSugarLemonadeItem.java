package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullSugarLemonadeItem extends DrinkItem {
    public FullSugarLemonadeItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.FULL_SUGAR_LEMONADE_HUNGER)
            .saturationModifier(FoodProperties.FULL_SUGAR_LEMONADE_SATURATION)
            .statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1200, 0), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 1200, 0), 1.0f)
            .build()));
    }
}
