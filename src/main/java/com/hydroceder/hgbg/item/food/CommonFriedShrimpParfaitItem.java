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

public class CommonFriedShrimpParfaitItem extends Item {

    public CommonFriedShrimpParfaitItem(FabricItemSettings settings) {
        super(settings
            .food(FoodProperties.createAlwaysEdibleFood(
                FoodProperties.COMMON_FRIED_SHRIMP_PARFAIT_HUNGER,
                FoodProperties.COMMON_FRIED_SHRIMP_PARFAIT_SATURATION
            ).build()));
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

            result = FoodProperties.handleBowlReturn(stack, world, player, result);
        }

        return result;
    }
}
