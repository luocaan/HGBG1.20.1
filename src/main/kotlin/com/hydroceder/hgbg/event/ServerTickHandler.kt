package com.hydroceder.hgbg.event

import net.minecraft.block.Blocks
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import java.util.UUID

object ServerTickHandler {
    private val SPEED_MODIFIER_UUID = UUID.fromString("35023419-1981-0114-5140-350234191981")

    private val lastDebuffClearTime = HashMap<UUID, Long>()
    private const val DEBUFF_CLEAR_COOLDOWN = 100L

    private val farmlandNearbyCache = HashMap<UUID, Boolean>()
    private val farmlandCheckCounter = HashMap<UUID, Int>()
    private const val FARMLAND_CHECK_INTERVAL = 20

    fun register() {
        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick { server ->
            for (player in server.playerManager.playerList) {
                handleNourishmentLevel2(player)
                handleSatiatedSprint(player)
                handleFieldHarvester(player)
            }
        })
    }

    private fun handleNourishmentLevel2(player: PlayerEntity) {
        if (ModEnchantments.hasNourishmentEnchantmentLevel2(player)) {
            com.hydroceder.hgbg.util.SaturationTracker.update(player)

            if (com.hydroceder.hgbg.util.SaturationTracker.isSaturationFullForLongEnough(player)) {
                val playerId = player.uuid
                val now = player.world.time
                val lastClear = lastDebuffClearTime[playerId] ?: 0L
                if (now - lastClear >= DEBUFF_CLEAR_COOLDOWN) {
                    if (com.hydroceder.hgbg.util.PotionEffectRemover.removeNegativeEffect(player)) {
                        com.hydroceder.hgbg.util.SaturationTracker.reset(player)
                        lastDebuffClearTime[playerId] = now
                    }
                }
            }
        }
    }

    private fun handleSatiatedSprint(player: PlayerEntity) {
        val speedLevel = ModEnchantments.getSpeedEnchantmentLevel(player)
        val hasSaturation = player.hungerManager.saturationLevel > 0.0f
        val isSprinting = player.isSprinting

        val speedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)

        if (speedAttribute != null) {
            val shouldApply = speedLevel > 0 && hasSaturation && isSprinting
            val currentlyApplied = speedAttribute.getModifier(SPEED_MODIFIER_UUID) != null

            if (shouldApply && !currentlyApplied) {
                val speedBonus = when (speedLevel) {
                    1 -> 0.2
                    2 -> 0.3
                    3 -> 0.4
                    else -> 0.0
                }

                val modifier = EntityAttributeModifier(
                    SPEED_MODIFIER_UUID,
                    "Satiated Sprint speed bonus",
                    speedBonus,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE
                )

                speedAttribute.addTemporaryModifier(modifier)
            } else if (!shouldApply && currentlyApplied) {
                speedAttribute.removeModifier(SPEED_MODIFIER_UUID)
            }
        }
    }

    private fun handleFieldHarvester(player: PlayerEntity) {
        val fieldHarvesterLevel = ModEnchantments.getFieldHarvesterEnchantmentLevel(player)
        val fieldPlayerId = player.uuid
        val counter = farmlandCheckCounter[fieldPlayerId] ?: 0
        if (counter >= FARMLAND_CHECK_INTERVAL) {
            farmlandCheckCounter[fieldPlayerId] = 0
            farmlandNearbyCache[fieldPlayerId] = isNearFarmland(player.world, player.blockPos)
        } else {
            farmlandCheckCounter[fieldPlayerId] = counter + 1
        }
        if (fieldHarvesterLevel > 0 && (farmlandNearbyCache[fieldPlayerId] ?: false)) {
            player.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, 60, 0, false, false))
        }
    }

    private fun isNearFarmland(world: World, pos: BlockPos): Boolean {
        for (x in -1..1) {
            for (y in -1..1) {
                for (z in -1..1) {
                    val checkPos = pos.add(x, y, z)
                    val state = world.getBlockState(checkPos)
                    if (state.isOf(Blocks.FARMLAND)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun cleanupPlayerData(playerId: UUID) {
        lastDebuffClearTime.remove(playerId)
        farmlandNearbyCache.remove(playerId)
        farmlandCheckCounter.remove(playerId)
    }
}
