package com.hydroceder.hgbg.seasoning

import com.hydroceder.hgbg.item.ModItems
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import org.slf4j.LoggerFactory

object ModSeasonings {
    private val logger = LoggerFactory.getLogger("hunger-begone")

    fun register() {
        SeasoningRegistry.register(SimpleSeasoning(
            "hunger-begone:salt",
            "item.hunger-begone.seasoned.salt",
            ModItems.SALT,
            { user ->
                user.addStatusEffect(StatusEffectInstance(
                    StatusEffects.SATURATION,
                    20,
                    0,
                    false,
                    true
                ))
            },
            true,
            false
        ))

        SeasoningRegistry.register(SimpleSeasoning(
            "hunger-begone:soy_sauce",
            "item.hunger-begone.seasoned.soy_sauce",
            ModItems.SOY_SAUCE,
            { user ->
                user.addStatusEffect(StatusEffectInstance(
                    StatusEffects.HASTE,
                    500,
                    0,
                    false,
                    true
                ))
            },
            true,
            true
        ))

        SeasoningRegistry.register(SimpleSeasoning(
            "hunger-begone:chili_sauce",
            "item.hunger-begone.seasoned.chili_sauce",
            ModItems.CHILI_BOTTLE,
            { user ->
                user.fireTicks = 60
                user.addStatusEffect(StatusEffectInstance(
                    StatusEffects.JUMP_BOOST,
                    700,
                    1,
                    false,
                    true
                ))
            },
            true,
            true
        ))

        SeasoningRegistry.register(SimpleSeasoning(
            "hunger-begone:cinnamon",
            "item.hunger-begone.seasoned.cinnamon",
            ModItems.CINNAMON,
            { user ->
                user.addStatusEffect(StatusEffectInstance(
                    StatusEffects.SPEED,
                    600,
                    0,
                    false,
                    true
                ))
            },
            true,
            false
        ))

        logger.info("Registered ${SeasoningRegistry.size()} seasonings!")
    }
}
