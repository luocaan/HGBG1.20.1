package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ShinyBerryJamBreadItem extends Item {
    public ShinyBerryJamBreadItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.SHINY_BERRY_JAM_BREAD_HUNGER,
            FoodProperties.SHINY_BERRY_JAM_BREAD_SATURATION
        )
            .statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 1200, 0), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 0), 1.0f)
            .build()));
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }
}
