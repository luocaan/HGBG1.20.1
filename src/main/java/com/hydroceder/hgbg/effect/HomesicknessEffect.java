package com.hydroceder.hgbg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

public class HomesicknessEffect extends StatusEffect {
    public static HomesicknessEffect INSTANCE;
    public static final Identifier ID = new Identifier("hunger-begone", "homesickness");
    
    public HomesicknessEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x8B4513);
    }
    
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
    
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }
}
