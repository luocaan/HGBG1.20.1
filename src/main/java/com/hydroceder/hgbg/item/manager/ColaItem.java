package com.hydroceder.hgbg.item.manager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ColaItem extends DrinkItem {
    public ColaItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        if (!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 1200, 0));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 0));
        }
        
        return result;
    }
}
