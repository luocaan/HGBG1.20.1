package com.hydroceder.hgbg;

import net.minecraft.entity.player.PlayerEntity;

/**
 * 模组主类的Java辅助类
 * 提供调试日志功能
 */
public class HgbgMod {
    public static final String MOD_ID = "hunger-begone";
    
    /**
     * 发送调试消息给玩家
     * 只有当玩家启用了调试模式时才会发送
     */
    public static void debugLog(PlayerEntity player, String message) {
        DebugManager.sendDebugMessage(player, message);
    }
}
