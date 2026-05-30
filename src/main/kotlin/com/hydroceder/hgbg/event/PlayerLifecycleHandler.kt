package com.hydroceder.hgbg.event

import com.hydroceder.hgbg.config.ModConfig
import com.hydroceder.hgbg.effect.HomesicknessEffect
import com.hydroceder.hgbg.item.ModItems
import com.hydroceder.hgbg.item.material.HomelandDirtItem
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import org.slf4j.LoggerFactory
import java.util.UUID

object PlayerLifecycleHandler {
    private val logger = LoggerFactory.getLogger("hunger-begone")

    private val homelandDirtCache = HashMap<UUID, Boolean>()
    private var homelandDirtCacheTickCounter = 0
    private const val HOMELAND_DIRT_CACHE_INTERVAL = 20

    fun register() {
        registerJoinEvent()
        registerDeathEvent()
        registerDamageEvent()
        registerHomesicknessTick()
    }

    private fun registerJoinEvent() {
        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEvents.Join { handler, sender, server ->
            val player = handler.player

            if (ModConfig.isGiveHomelandDirtEnabled()) {
                var hasHomelandDirt = false
                for (stack in player.inventory.main) {
                    if (stack.item == ModItems.HOMELAND_DIRT) {
                        hasHomelandDirt = true
                        break
                    }
                }

                if (!hasHomelandDirt && player.offHandStack.item == ModItems.HOMELAND_DIRT) {
                    hasHomelandDirt = true
                }

                if (!hasHomelandDirt) {
                    val homelandDirt = ItemStack(ModItems.HOMELAND_DIRT, 1)
                    if (!player.inventory.insertStack(homelandDirt)) {
                        player.dropItem(homelandDirt, false)
                    }
                    logger.info("Gave homeland dirt to new player: {}", player.name.string)
                }
            }
        })
    }

    private fun registerDeathEvent() {
        ServerLivingEntityEvents.ALLOW_DEATH.register(ServerLivingEntityEvents.AllowDeath { entity, damageSource, damageAmount ->
            if (entity is PlayerEntity) {
                if (damageSource.isOf(DamageTypes.GENERIC_KILL)) {
                    return@AllowDeath true
                }

                if (HomelandDirtItem.tryUseHomelandDirt(entity, entity.world)) {
                    return@AllowDeath false
                }
            }
            return@AllowDeath true
        })
    }

    private fun registerDamageEvent() {
        val processingDamage = HashSet<UUID>()
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(ServerLivingEntityEvents.AllowDamage { entity, damageSource, amount ->
            if (entity is PlayerEntity && entity.hasStatusEffect(HomesicknessEffect.INSTANCE)) {
                if (damageSource.isOf(DamageTypes.OUT_OF_WORLD) || damageSource.isOf(DamageTypes.GENERIC_KILL)) {
                    return@AllowDamage true
                }

                val playerId = entity.uuid
                if (processingDamage.contains(playerId)) {
                    return@AllowDamage true
                }

                val maxHealth = entity.maxHealth
                if (amount > maxHealth) {
                    val reducedDamage = maxHealth / 2.0f
                    try {
                        processingDamage.add(playerId)
                        val hadEffect = entity.hasStatusEffect(HomesicknessEffect.INSTANCE)
                        entity.removeStatusEffect(HomesicknessEffect.INSTANCE)
                        entity.damage(damageSource, reducedDamage)
                        if (hadEffect) {
                            entity.addStatusEffect(StatusEffectInstance(HomesicknessEffect.INSTANCE, 100, 0, false, false))
                        }
                    } finally {
                        processingDamage.remove(playerId)
                    }
                    return@AllowDamage false
                }
            }
            return@AllowDamage true
        })
    }

    private fun registerHomesicknessTick() {
        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick { server ->
            homelandDirtCacheTickCounter = (homelandDirtCacheTickCounter + 1) % HOMELAND_DIRT_CACHE_INTERVAL
            val shouldUpdateHomelandDirtCache = homelandDirtCacheTickCounter == 0

            for (player in server.playerManager.playerList) {
                val playerId = player.uuid

                if (shouldUpdateHomelandDirtCache) {
                    var found = false
                    for (stack in player.inventory.main) {
                        if (stack.item == ModItems.HOMELAND_DIRT) {
                            found = true
                            break
                        }
                    }
                    if (!found && player.offHandStack.item == ModItems.HOMELAND_DIRT) {
                        found = true
                    }
                    homelandDirtCache[playerId] = found
                }
                val hasHomelandDirt = homelandDirtCache[playerId] ?: false

                val x = kotlin.math.abs(player.x).toInt()
                val y = kotlin.math.abs(player.y).toInt()
                val z = kotlin.math.abs(player.z).toInt()

                if (x > 1000 || y > 1000 || z > 1000) {
                    com.hydroceder.hgbg.advancement.DistanceTrigger.getInstance().trigger(player as ServerPlayerEntity)
                }

                if (player.isSprinting) {
                    com.hydroceder.hgbg.advancement.RunTrigger.getInstance().trigger(player as ServerPlayerEntity)
                }

                if (hasHomelandDirt) {
                    val isNotOverworld = player.world.registryKey != World.OVERWORLD
                    if (isNotOverworld || x > 30000 || y > 30000 || z > 30000) {
                        player.addStatusEffect(StatusEffectInstance(HomesicknessEffect.INSTANCE, 100, 0, false, false))
                    }
                }
            }
        })
    }

    fun cleanupPlayerData(playerId: UUID) {
        homelandDirtCache.remove(playerId)
    }
}
