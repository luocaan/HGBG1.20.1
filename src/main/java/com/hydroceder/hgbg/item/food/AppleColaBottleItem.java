package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.ColaItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import java.util.List;

public class AppleColaBottleItem extends ColaItem {
    public AppleColaBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.APPLE_COLA_HUNGER)
            .saturationModifier(FoodProperties.APPLE_COLA_SATURATION)
            .build()));
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, net.minecraft.client.item.TooltipContext context) {
        tooltip.add(Text.translatable("item.hunger-begone.cola.tooltip").formatted(Formatting.DARK_PURPLE));
    }
}
