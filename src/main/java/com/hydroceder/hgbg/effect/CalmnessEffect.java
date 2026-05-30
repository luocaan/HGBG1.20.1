package com.hydroceder.hgbg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CalmnessEffect extends StatusEffect {
    public static CalmnessEffect INSTANCE;
    public static final String ID_STRING = "calmness";
    public static final net.minecraft.util.Identifier ID = new net.minecraft.util.Identifier("hunger-begone", ID_STRING);

    private static final Map<UUID, Double> accumulatedDamageMap = new HashMap<>();
    private static final Map<UUID, Double> accumulatedHealMap = new HashMap<>();
    private static final Set<UUID> settlingPlayers = new HashSet<>();
    private static final Set<UUID> refreshingPlayers = new HashSet<>();

    public CalmnessEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x87CEEB);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        boolean isPlayer = entity instanceof PlayerEntity;

        // 玩家专属：饱和度低于1时停止续期，让效果自然消失
        if (isPlayer) {
            PlayerEntity player = (PlayerEntity) entity;
            if (player.getHungerManager().getSaturationLevel() < 1.0f) {
                return;
            }

            // 每5 tick增加20疲劳值（仅玩家）
            if (entity.age % 5 == 0) {
                player.getHungerManager().addExhaustion(20.0f);
            }
        }

        // 持续给予2秒冷静效果，使其不会消失（所有生物通用）
        StatusEffectInstance current = entity.getStatusEffect(INSTANCE);
        if (current != null && current.getDuration() <= 20) {
            refreshingPlayers.add(entity.getUuid());
            try {
                entity.addStatusEffect(new StatusEffectInstance(INSTANCE, 40, 0, false, false, true));
            } finally {
                refreshingPlayers.remove(entity.getUuid());
            }
        }
    }

    public static boolean isRefreshing(LivingEntity entity) {
        return refreshingPlayers.contains(entity.getUuid());
    }

    public static boolean hasCalmness(LivingEntity entity) {
        return entity.hasStatusEffect(INSTANCE);
    }

    public static boolean isSettling(LivingEntity entity) {
        return settlingPlayers.contains(entity.getUuid());
    }

    public static double getAccumulatedDamage(LivingEntity entity) {
        return accumulatedDamageMap.getOrDefault(entity.getUuid(), 0.0);
    }

    public static void addAccumulatedDamage(LivingEntity entity, double damage) {
        UUID uuid = entity.getUuid();
        accumulatedDamageMap.put(uuid, getAccumulatedDamage(entity) + damage);
    }

    public static double getAccumulatedHeal(LivingEntity entity) {
        return accumulatedHealMap.getOrDefault(entity.getUuid(), 0.0);
    }

    public static void addAccumulatedHeal(LivingEntity entity, double heal) {
        UUID uuid = entity.getUuid();
        accumulatedHealMap.put(uuid, getAccumulatedHeal(entity) + heal);
    }

    public static void clearAccumulatedData(LivingEntity entity) {
        UUID uuid = entity.getUuid();
        accumulatedDamageMap.remove(uuid);
        accumulatedHealMap.remove(uuid);
    }

    public static void settleCalmness(LivingEntity entity) {
        UUID uuid = entity.getUuid();

        if (settlingPlayers.contains(uuid)) {
            return;
        }

        settlingPlayers.add(uuid);
        try {
            double accumulatedDamage = getAccumulatedDamage(entity);
            double accumulatedHeal = getAccumulatedHeal(entity);
            double netChange = accumulatedHeal - accumulatedDamage;

            if (netChange > 0.001) {
                entity.heal((float) netChange);
            } else if (netChange < -0.001) {
                entity.damage(entity.getDamageSources().genericKill(), (float) (-netChange));
            }
        } finally {
            settlingPlayers.remove(uuid);
            clearAccumulatedData(entity);
        }
    }

    public static void cleanupPlayer(UUID playerId) {
        accumulatedDamageMap.remove(playerId);
        accumulatedHealMap.remove(playerId);
        settlingPlayers.remove(playerId);
        refreshingPlayers.remove(playerId);
    }
}
