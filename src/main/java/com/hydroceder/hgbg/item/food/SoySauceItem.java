package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class SoySauceItem extends DrinkItem {
    public SoySauceItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.SOY_SAUCE_HUNGER)
            .saturationModifier(FoodProperties.SOY_SAUCE_SATURATION)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0), 1.0f)
            .build()));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, net.minecraft.client.item.TooltipContext context) {
        tooltip.add(Text.translatable("item.hunger-begone.soy_sauce.tooltip").formatted(Formatting.BLUE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}