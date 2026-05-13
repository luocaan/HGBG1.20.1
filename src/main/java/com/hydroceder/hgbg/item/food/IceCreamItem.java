package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.effect.CalmnessEffect;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class IceCreamItem extends Item {
    public IceCreamItem(FabricItemSettings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.ICECREAM_HUNGER,
            FoodProperties.ICECREAM_SATURATION
        ).build()));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 24;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient && user instanceof PlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(
                CalmnessEffect.INSTANCE,
                100,
                0,
                false,
                true
            ));
            
            if (user.isOnFire()) {
                user.extinguish();
            }
            
            result = FoodProperties.handleBowlReturn(stack, world, player, result);
        }

        return result;
    }
}
