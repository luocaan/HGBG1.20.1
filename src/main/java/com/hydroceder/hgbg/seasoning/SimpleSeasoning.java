package com.hydroceder.hgbg.seasoning;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;

import java.util.function.Consumer;

/**
 * 简单的调味料实现类
 * 用于快速创建基本的调味料
 */
public class SimpleSeasoning implements Seasoning {
    private final String id;
    private final String translationKey;
    private final Item item;
    private final Consumer<LivingEntity> effectApplier;

    public SimpleSeasoning(String id, String translationKey, Item item, Consumer<LivingEntity> effectApplier) {
        this.id = id;
        this.translationKey = translationKey;
        this.item = item;
        this.effectApplier = effectApplier;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public void applyEffect(LivingEntity user) {
        if (effectApplier != null) {
            effectApplier.accept(user);
        }
    }
}
