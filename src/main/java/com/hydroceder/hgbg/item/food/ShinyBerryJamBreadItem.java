package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShinyBerryJamBreadItem extends Item {
    public ShinyBerryJamBreadItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.SHINY_BERRY_JAM_BREAD_HUNGER,
            FoodProperties.SHINY_BERRY_JAM_BREAD_SATURATION
        ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        if (!world.isClient && user instanceof PlayerEntity) {
            // 1分钟 = 60 * 20 = 1200 ticks
            // 发光效果
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 1200, 0, false, true));
            // 抗性提升I (amplifier = 0)
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 0, false, true));
        }
        
        return result;
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }
}
