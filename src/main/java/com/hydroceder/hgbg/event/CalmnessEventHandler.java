package com.hydroceder.hgbg.event;

import com.hydroceder.hgbg.DebugManager;
import com.hydroceder.hgbg.effect.CalmnessEffect;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CalmnessEventHandler {

    private static final Set<UUID> processingDamage = new HashSet<>();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static void register() {
        registerDamageHandler();
    }

    private static void registerDamageHandler() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player && CalmnessEffect.hasCalmness(player)) {
                UUID playerId = player.getUuid();

                if (processingDamage.contains(playerId)) {
                    return true;
                }

                if (source.isOf(DamageTypes.OUT_OF_WORLD)) {
                    return true;
                }

                if (amount > 0) {
                    try {
                        processingDamage.add(playerId);
                        CalmnessEffect.addAccumulatedDamage(player, amount);

                        if (DebugManager.isDebugEnabled(player)) {
                            double total = CalmnessEffect.getAccumulatedDamage(player);
                            DebugManager.sendDebugMessage(player, "§b[BUFF]§f累计伤害：" + DECIMAL_FORMAT.format(total));
                        }
                    } finally {
                        processingDamage.remove(playerId);
                    }
                    return false;
                }
            }
            return true;
        });
    }
}
