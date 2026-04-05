package com.hydroceder.hgbg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

/**
 * 迟滞药水效果
 * 使被赋予该药水效果的目标每20tick受到4点魔法伤害
 */
public class SlownessPotionEffect extends StatusEffect {
    public static SlownessPotionEffect INSTANCE;
    public static final Identifier ID = new Identifier("hunger-begone", "slowness_effect");
    
    public SlownessPotionEffect() {
        super(StatusEffectCategory.HARMFUL, 0x996600);
    }
    
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // 每20tick（1秒）触发一次效果
        return duration % 20 == 0;
    }
    
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // 每20tick对目标造成4点魔法伤害
        entity.damage(entity.getDamageSources().magic(), 4.0f);
    }
}