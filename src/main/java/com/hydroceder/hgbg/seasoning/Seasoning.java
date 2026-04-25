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
}
