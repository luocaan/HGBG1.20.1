package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class StewHotchpotchItem extends Item {
    private static final int EFFECT_DURATION = 200;

    private static final StatusEffect[] SAFE_EFFECTS = {
        StatusEffects.SPEED,
        StatusEffects.HASTE,
        StatusEffects.JUMP_BOOST,
        StatusEffects.NIGHT_VISION,
        StatusEffects.REGENERATION,
        StatusEffects.RESISTANCE,
        StatusEffects.FIRE_RESISTANCE,
        StatusEffects.WATER_BREATHING,
        StatusEffects.ABSORPTION,
        StatusEffects.WEAKNESS,
        StatusEffects.MINING_FATIGUE,
        StatusEffects.SLOWNESS,
        StatusEffects.NAUSEA,
        StatusEffects.BLINDNESS,
        StatusEffects.HUNGER
    };

    public StewHotchpotchItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.STEW_HOTCHPOTCH_HUNGER,
            FoodProperties.STEW_HOTCHPOTCH_SATURATION
        ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient) {
            applyRandomSafeEffect(user);
        }

        return FoodProperties.handleBowlReturn(stack, world, user, result);
    }

    private void applyRandomSafeEffect(LivingEntity entity) {
        List<StatusEffect> available = new ArrayList<>();
        for (StatusEffect effect : SAFE_EFFECTS) {
            available.add(effect);
        }

        if (!available.isEmpty()) {
            StatusEffect chosen = available.get(entity.getRandom().nextInt(available.size()));
            entity.addStatusEffect(new StatusEffectInstance(chosen, EFFECT_DURATION, 0));
        }
    }
}
