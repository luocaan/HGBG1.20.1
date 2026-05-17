package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class HotChocolateBottleItem extends DrinkItem {
    public HotChocolateBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.HOT_CHOCOLATE_HUNGER)
            .saturationModifier(FoodProperties.HOT_CHOCOLATE_SATURATION)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1200, 1), 1.0f)
            .build()));
    }
}
