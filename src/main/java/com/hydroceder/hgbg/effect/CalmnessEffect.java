package com.hydroceder.hgbg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

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
        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }

        // 如果饱和度低于1，停止给予效果，让其自然消失
        if (player.getHungerManager().getSaturationLevel() < 1.0f) {
            return;
        }

        // 每5 tick增加20疲劳值
        if (entity.age % 5 == 0) {
            player.getHungerManager().addExhaustion(20.0f);
        }

        // 持续给予2秒冷静效果，使其不会消失
        StatusEffectInstance current = player.getStatusEffect(INSTANCE);
        if (current != null && current.getDuration() <= 20) {
            refreshingPlayers.add(player.getUuid());
            try {
                player.addStatusEffect(new StatusEffectInstance(INSTANCE, 40, 0, false, false, true));
            } finally {
                refreshingPlayers.remove(player.getUuid());
            }
        }
    }

    public static boolean isRefreshing(PlayerEntity player) {
        return refreshingPlayers.contains(player.getUuid());
    }

    public static boolean hasCalmness(PlayerEntity player) {
        return player.hasStatusEffect(INSTANCE);
    }

    public static boolean isSettling(PlayerEntity player) {
        return settlingPlayers.contains(player.getUuid());
    }

    public static double getAccumulatedDamage(PlayerEntity player) {
        return accumulatedDamageMap.getOrDefault(player.getUuid(), 0.0);
    }

    public static void addAccumulatedDamage(PlayerEntity player, double damage) {
        UUID uuid = player.getUuid();
        accumulatedDamageMap.put(uuid, getAccumulatedDamage(player) + damage);
    }

    public static double getAccumulatedHeal(PlayerEntity player) {
        return accumulatedHealMap.getOrDefault(player.getUuid(), 0.0);
    }

    public static void addAccumulatedHeal(PlayerEntity player, double heal) {
        UUID uuid = player.getUuid();
        accumulatedHealMap.put(uuid, getAccumulatedHeal(player) + heal);
    }

    public static void clearAccumulatedData(PlayerEntity player) {
        UUID uuid = player.getUuid();
        accumulatedDamageMap.remove(uuid);
        accumulatedHealMap.remove(uuid);
    }

    public static void settleCalmness(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();

        if (settlingPlayers.contains(uuid)) {
            return;
        }

        settlingPlayers.add(uuid);
        try {
            double accumulatedDamage = getAccumulatedDamage(player);
            double accumulatedHeal = getAccumulatedHeal(player);
            double netChange = accumulatedHeal - accumulatedDamage;

            if (netChange > 0.001) {
                player.heal((float) netChange);
            } else if (netChange < -0.001) {
                player.damage(player.getDamageSources().magic(), (float) (-netChange));
            }
        } finally {
            settlingPlayers.remove(uuid);
            clearAccumulatedData(player);
        }
    }

    public static void cleanupPlayer(UUID playerId) {
        accumulatedDamageMap.remove(playerId);
        accumulatedHealMap.remove(playerId);
        settlingPlayers.remove(playerId);
        refreshingPlayers.remove(playerId);
    }
}
