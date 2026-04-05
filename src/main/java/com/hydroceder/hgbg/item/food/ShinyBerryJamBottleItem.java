package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShinyBerryJamBottleItem extends DrinkItem {
    public ShinyBerryJamBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.SHINY_BERRY_JAM_HUNGER)
            .saturationModifier(FoodProperties.SHINY_BERRY_JAM_SATURATION)
            .build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        if (!world.isClient && user instanceof PlayerEntity) {
            // 1分钟 = 60 * 20 = 1200 ticks
            // 发光效果
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 1200, 0, false, true));
        }
        
        return result;
    }
}
