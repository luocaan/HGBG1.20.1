package com.hydroceder.hgbg.event;

import com.hydroceder.hgbg.effect.CalmnessEffect;
import com.hydroceder.hgbg.util.SaturationTracker;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.UUID;

public class PlayerDisconnectHandler {

    public static void register() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (handler.player != null) {
                cleanupPlayerData(handler.player);
            }
        });
    }

    private static void cleanupPlayerData(ServerPlayerEntity player) {
        if (player == null) return;

        UUID playerId = player.getUuid();

        CalmnessEffect.cleanupPlayer(playerId);
        SaturationTracker.cleanupPlayer(playerId);
    }
}
