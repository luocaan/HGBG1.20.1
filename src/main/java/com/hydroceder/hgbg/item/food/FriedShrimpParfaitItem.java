package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.effect.CalmnessEffect;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FriedShrimpParfaitItem extends Item {

    public FriedShrimpParfaitItem(FabricItemSettings settings) {
        super(settings
            .food(FoodProperties.createAlwaysEdibleFood(
                FoodProperties.FRIED_SHRIMP_PARFAIT_HUNGER,
                FoodProperties.FRIED_SHRIMP_PARFAIT_SATURATION
            ).build())
            .rarity(net.minecraft.util.Rarity.EPIC));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 64;
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
            player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.ABSORPTION,
                2400,
                3,
                false,
                true
            ));
            player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.REGENERATION,
                400,
                1,
                false,
                true
            ));
            player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.FIRE_RESISTANCE,
                6000,
                0,
                false,
                true
            ));
            player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.RESISTANCE,
                6000,
                0,
                false,
                true
            ));

            result = FoodProperties.handleBowlReturn(stack, world, player, result);
        }

        return result;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
