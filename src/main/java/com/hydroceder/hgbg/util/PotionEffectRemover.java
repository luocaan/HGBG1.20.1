package com.hydroceder.hgbg.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 药水效果移除器
 * 用于移除玩家的负面效果
 */
public class PotionEffectRemover {
    private static final Logger LOGGER = LoggerFactory.getLogger("hunger-begone");
    private static final Random RANDOM = new Random();
    
    /**
     * 尝试从玩家身上移除一个负面效果
     * @param player 玩家实体
     * @return true 表示成功移除了一个负面效果
     */
    public static boolean removeNegativeEffect(PlayerEntity player) {
        if (player == null) return false;
        
        // 获取玩家的所有负面效果
        List<StatusEffectInstance> negativeEffects = getNegativeEffects(player);
        
        if (negativeEffects.isEmpty()) {
            return false;
        }
        
        // 随机选择一个负面效果
        StatusEffectInstance effectToRemove = negativeEffects.get(RANDOM.nextInt(negativeEffects.size()));
        StatusEffect effectType = effectToRemove.getEffectType();
        
        // 移除效果
        player.removeStatusEffect(effectType);
        LOGGER.info("Removed negative effect {} from player {}", effectType.getName().getString(), player.getName().getString());
        
        return true;
    }
    
    /**
     * 获取玩家的所有负面效果
     * @param player 玩家实体
     * @return 负面效果列表
     */
    private static List<StatusEffectInstance> getNegativeEffects(PlayerEntity player) {
        List<StatusEffectInstance> negativeEffects = new ArrayList<>();
        
        for (StatusEffectInstance effect : player.getStatusEffects()) {
            if (effect.getEffectType().isBeneficial()) {
                // 正面效果，跳过
                continue;
            } else {
                // 负面效果，添加到列表
                negativeEffects.add(effect);
            }
        }
        
        return negativeEffects;
    }
}