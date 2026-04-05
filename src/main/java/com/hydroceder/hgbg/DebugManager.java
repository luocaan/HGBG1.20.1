package com.hydroceder.hgbg;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 调试管理类
 * 管理调试模式和调试消息发送
 */
public class DebugManager {
    private static final Set<UUID> debugPlayers = new HashSet<>();
    
    /**
     * 启用玩家的调试模式
     */
    public static void enableDebug(PlayerEntity player) {
        debugPlayers.add(player.getUuid());
    }
    
    /**
     * 禁用玩家的调试模式
     */
    public static void disableDebug(PlayerEntity player) {
        debugPlayers.remove(player.getUuid());
    }
    
    /**
     * 检查玩家是否启用了调试模式
     */
    public static boolean isDebugEnabled(PlayerEntity player) {
        return debugPlayers.contains(player.getUuid());
    }
    
    /**
     * 发送调试消息给指定玩家
     */
    public static void sendDebugMessage(PlayerEntity player, String message) {
        if (isDebugEnabled(player) && player instanceof ServerPlayerEntity) {
            player.sendMessage(Text.literal("§6[HGBG Debug] §f" + message), false);
        }
    }
    
    /**
     * 广播调试消息给所有启用了调试的玩家
     */
    public static void broadcastDebugMessage(String message) {
        // 此方法用于广播给所有调试玩家，但当前实现中我们使用直接发送
    }
}
