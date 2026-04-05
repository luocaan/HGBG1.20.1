package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MushroomOilItem extends DrinkItem {
    public MushroomOilItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.MUSHROOM_OIL_HUNGER)
            .saturationModifier(FoodProperties.MUSHROOM_OIL_SATURATION)
            .build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        if (!world.isClient && user instanceof PlayerEntity) {
            // 8秒 = 8 * 20 = 160 ticks
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 160, 0, false, true));
        }
        
        return result;
    }
}
