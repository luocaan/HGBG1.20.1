package com.hydroceder.hgbg.event;

import com.hydroceder.hgbg.DebugManager;
import com.hydroceder.hgbg.effect.CalmnessEffect;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CalmnessEventHandler {

    private static final Set<UUID> processingDamage = new HashSet<>();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final double DAMAGE_THRESHOLD = 240.0;

    public static void register() {
        registerDamageHandler();
        registerDeathHandler();
    }

    private static void registerDamageHandler() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (CalmnessEffect.hasCalmness(entity)) {
                UUID entityId = entity.getUuid();

                if (processingDamage.contains(entityId)) {
                    return true;
                }

                if (source.isOf(DamageTypes.OUT_OF_WORLD) || source.isOf(DamageTypes.GENERIC_KILL)) {
                    return true;
                }

                if (amount > 0) {
                    double currentTotal = CalmnessEffect.getAccumulatedDamage(entity);

                    if (currentTotal >= DAMAGE_THRESHOLD) {
                        return true;
                    }

                    try {
                        processingDamage.add(entityId);
                        double remaining = DAMAGE_THRESHOLD - currentTotal;
                        double toAccumulate = Math.min(amount, remaining);
                        CalmnessEffect.addAccumulatedDamage(entity, toAccumulate);

                        if (entity instanceof ServerPlayerEntity player && DebugManager.isDebugEnabled(player)) {
                            double total = CalmnessEffect.getAccumulatedDamage(entity);
                            DebugManager.sendDebugMessage(player, "§b[BUFF]§f累计伤害：" + DECIMAL_FORMAT.format(total) + " / " + DECIMAL_FORMAT.format(DAMAGE_THRESHOLD));
                        }
                    } finally {
                        processingDamage.remove(entityId);
                    }
                    return false;
                }
            }
            return true;
        });
    }

    private static void registerDeathHandler() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (CalmnessEffect.hasCalmness(entity)) {
                CalmnessEffect.cleanupPlayer(entity.getUuid());
            }
        });
    }
}
