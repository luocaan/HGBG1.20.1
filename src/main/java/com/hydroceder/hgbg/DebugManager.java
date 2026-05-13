package com.hydroceder.hgbg;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DebugManager {
    private static final Set<UUID> debugPlayers = new HashSet<>();
    
    public static void enableDebug(PlayerEntity player) {
        debugPlayers.add(player.getUuid());
    }
    
    public static void disableDebug(PlayerEntity player) {
        debugPlayers.remove(player.getUuid());
    }
    
    public static boolean isDebugEnabled(PlayerEntity player) {
        return debugPlayers.contains(player.getUuid());
    }
    
    public static void sendDebugMessage(PlayerEntity player, String message) {
        if (isDebugEnabled(player) && player instanceof ServerPlayerEntity) {
            player.sendMessage(Text.literal("\u00a76[HGBG Debug] \u00a7f" + message), false);
        }
    }
    
    public static void cleanupPlayer(UUID playerId) {
        debugPlayers.remove(playerId);
    }
}
