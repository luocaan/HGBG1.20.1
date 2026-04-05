package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.ColaItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import java.util.List;

public class ShinyBerryColaBottleItem extends ColaItem {
    public ShinyBerryColaBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.SHINY_BERRY_COLA_HUNGER)
            .saturationModifier(FoodProperties.SHINY_BERRY_COLA_SATURATION)
            .build()));
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, net.minecraft.client.item.TooltipContext context) {
        tooltip.add(Text.translatable("item.hunger-begone.cola.tooltip").formatted(Formatting.DARK_PURPLE));
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        // 饮用后获得1200ticks（60秒）的发光效果
        if (!world.isClient && user instanceof PlayerEntity) {
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.GLOWING,
                    1200, // 1200 ticks = 60 seconds
                    0,    // amplifier
                    false, // ambient
                    true   // show particles
            ));
        }
        
        return result;
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
