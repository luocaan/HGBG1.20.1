package com.hydroceder.hgbg.util;

import net.minecraft.entity.player.PlayerEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 饱和度跟踪器
 * 用于跟踪玩家的饱和度状态和持续时间
 */
public class SaturationTracker {
    private static final Map<UUID, Long> saturationFullStartTime = new HashMap<>();
    private static final long SATURATION_FULL_DURATION = 5 * 20; // 5秒，以刻为单位（1秒=20刻）
    
    /**
     * 更新玩家的饱和度状态
     * @param player 玩家实体
     */
    public static void update(PlayerEntity player) {
        if (player == null) return;
        
        UUID playerId = player.getUuid();
        boolean isSaturationFull = isSaturationFull(player);
        
        if (isSaturationFull) {
            // 饱和度满，记录开始时间
            if (!saturationFullStartTime.containsKey(playerId)) {
                saturationFullStartTime.put(playerId, player.getWorld().getTime());
            }
        } else {
            // 饱和度不满，重置开始时间
            saturationFullStartTime.remove(playerId);
        }
    }
    
    /**
     * 检查玩家的饱和度是否满且持续时间超过5秒
     * @param player 玩家实体
     * @return true 表示饱和度满且持续时间超过5秒
     */
    public static boolean isSaturationFullForLongEnough(PlayerEntity player) {
        if (player == null) return false;
        
        UUID playerId = player.getUuid();
        if (!saturationFullStartTime.containsKey(playerId)) {
            return false;
        }
        
        long startTime = saturationFullStartTime.get(playerId);
        long currentTime = player.getWorld().getTime();
        
        return currentTime - startTime >= SATURATION_FULL_DURATION;
    }
    
    /**
     * 检查玩家的饱和度是否满
     * @param player 玩家实体
     * @return true 表示饱和度满
     */
    private static boolean isSaturationFull(PlayerEntity player) {
        return player.getHungerManager().getSaturationLevel() >= player.getHungerManager().getFoodLevel();
    }
    
    /**
     * 重置玩家的饱和度状态
     * @param player 玩家实体
     */
    public static void reset(PlayerEntity player) {
        if (player != null) {
            saturationFullStartTime.remove(player.getUuid());
        }
    }
    
    /**
     * 清理玩家数据（玩家退出时调用）
     * @param playerId 玩家UUID
     */
    public static void cleanupPlayer(UUID playerId) {
        if (playerId != null) {
            saturationFullStartTime.remove(playerId);
        }
    }
}