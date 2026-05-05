package com.hydroceder.hgbg.effect;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模组药水效果注册类
 */
public class ModEffects {
    private static final Logger LOGGER = LoggerFactory.getLogger("hunger-begone");
    
    /**
     * 注册所有药水效果
     */
    public static void register() {
        // 注册迟滞药水效果
        SlownessPotionEffect.INSTANCE = new SlownessPotionEffect();
        Registry.register(Registries.STATUS_EFFECT, SlownessPotionEffect.ID, SlownessPotionEffect.INSTANCE);
        LOGGER.info("Slowness potion effect registered successfully!");
        
        // 注册归心效果
        HomesicknessEffect.INSTANCE = new HomesicknessEffect();
        Registry.register(Registries.STATUS_EFFECT, HomesicknessEffect.ID, HomesicknessEffect.INSTANCE);
        LOGGER.info("Homesickness effect registered successfully!");

        // 注册冷静效果
        CalmnessEffect.INSTANCE = new CalmnessEffect();
        Registry.register(Registries.STATUS_EFFECT, CalmnessEffect.ID, CalmnessEffect.INSTANCE);
        LOGGER.info("Calmness effect registered successfully!");
    }
}