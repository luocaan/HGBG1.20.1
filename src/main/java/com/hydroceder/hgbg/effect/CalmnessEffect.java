package com.hydroceder.hgbg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

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
    private static final Set<UUID> settlingPlayers = new HashSet<>();

    public CalmnessEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x87CEEB);
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

    public static void clearAccumulatedData(LivingEntity entity) {
        UUID uuid = entity.getUuid();
        accumulatedDamageMap.remove(uuid);
    }

    public static void settleCalmness(LivingEntity entity) {
        UUID uuid = entity.getUuid();

        if (settlingPlayers.contains(uuid)) {
            return;
        }

        settlingPlayers.add(uuid);
        try {
            double accumulatedDamage = getAccumulatedDamage(entity);
            if (accumulatedDamage > 0.001) {
                entity.damage(entity.getDamageSources().genericKill(), (float) accumulatedDamage);
            }
        } finally {
            settlingPlayers.remove(uuid);
            clearAccumulatedData(entity);
        }
    }

    public static void cleanupPlayer(UUID playerId) {
        accumulatedDamageMap.remove(playerId);
        settlingPlayers.remove(playerId);
    }
}
