package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SoybeanMilkItem extends Item {
    private static final int REGENERATION_DURATION = 600;

    public SoybeanMilkItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.SOYBEAN_MILK_HUNGER,
            FoodProperties.SOYBEAN_MILK_SATURATION
        )
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, REGENERATION_DURATION, 0), 1.0f)
            .build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient && user instanceof PlayerEntity) {
            clearRandomNegativeEffect(user);
        }

        return FoodProperties.handleBowlReturn(stack, world, user, result);
    }

    private void clearRandomNegativeEffect(LivingEntity entity) {
        List<StatusEffect> negativeEffects = new ArrayList<>();

        for (StatusEffectInstance effect : entity.getStatusEffects()) {
            StatusEffect type = effect.getEffectType();
            if (type.getCategory() == StatusEffectCategory.HARMFUL && !type.equals(StatusEffects.WITHER)) {
                negativeEffects.add(type);
            }
        }

        if (!negativeEffects.isEmpty()) {
            StatusEffect toRemove = negativeEffects.get(entity.getRandom().nextInt(negativeEffects.size()));
            entity.removeStatusEffect(toRemove);
        }
    }
}
