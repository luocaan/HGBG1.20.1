package com.hydroceder.hgbg.seasoning;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;

/**
 * 调味料接口
 * 定义调味料的基本行为
 */
public interface Seasoning {
    /**
     * 获取调味料的唯一标识符
     */
    String getId();
    
    /**
     * 获取调味料的翻译键
     */
    String getTranslationKey();
    
    /**
     * 获取对应的物品
     */
    Item getItem();
    
    /**
     * 应用调味料效果
     */
    void applyEffect(LivingEntity user);
    
    /**
     * 检查该调味料是否可盛放入杯子
     * @return true 表示可盛放
     */
    default boolean isStorable() {
        return false;
    }
    
    /**
     * 检查该调味料是否为瓶装
     * 瓶装调味料：
     * - 存入时返还空瓶子
     * - 取出时需要空瓶子
     * - 播放水桶音效
     * 非瓶装调味料：
     * - 存入时不返还任何东西
     * - 取出时可直接徒手取出
     * - 播放沙子破坏音效
     * @return true 表示瓶装
     */
    default boolean isBottled() {
        return false;
    }
}