package com.hydroceder.hgbg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
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

    public CalmnessEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x87CEEB);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
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
    }
}
