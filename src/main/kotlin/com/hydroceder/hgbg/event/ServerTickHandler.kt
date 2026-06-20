package com.hydroceder.hgbg.event

import net.minecraft.block.Blocks
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import com.hydroceder.hgbg.damage.ModDamageTypes
import com.hydroceder.hgbg.item.ModItems
import net.minecraft.registry.RegistryKey
import java.util.UUID

object ServerTickHandler {
    private val SPEED_MODIFIER_UUID = UUID.fromString("35023419-1981-0114-5140-350234191981")

    private val lastDebuffClearTime = HashMap<UUID, Long>()
    private const val DEBUFF_CLEAR_COOLDOWN = 100L

    private val farmlandNearbyCache = HashMap<UUID, Boolean>()
    private val farmlandCheckCounter = HashMap<UUID, Int>()
    private const val FARMLAND_CHECK_INTERVAL = 20
    private val prevOnGround = HashMap<UUID, Boolean>()

    // Heavy pressure damage state
    private val heavyPressureTickCounter = HashMap<UUID, Int>()
    private const val HEAVY_PRESSURE_INTERVAL = 40
    private const val HEAVY_PRESSURE_DAMAGE = 4.0f

    fun register() {
        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick { server ->
            for (player in server.playerManager.playerList) {
                handleNourishmentLevel2(player)
                handleSatiatedSprint(player)
                handleFieldHarvester(player)
                handleBasketSlowness(player)
            }
        })
    }

    private fun handleBasketSlowness(player: PlayerEntity) {
        // --- Calculate total weight from all baskets ---
        var totalAmplifier = 0
        var forceMax = false // true when any stored entity exceeds 5-block collision

        for (stack in player.inventory.main) {
            val (contrib, large) = readBasketContribution(stack)
            totalAmplifier += contrib
            if (large) forceMax = true
        }

        // Also check off-hand slot
        val (offContrib, offLarge) = readBasketContribution(player.inventory.offHand[0])
        totalAmplifier += offContrib
        if (offLarge) forceMax = true

        if (forceMax) totalAmplifier = 16

        // --- Apply or clear effects ---
        if (totalAmplifier > 0) {
            applyBasketEffects(player, totalAmplifier)
        } else {
            player.removeStatusEffect(StatusEffects.SLOWNESS)
            heavyPressureTickCounter.remove(player.uuid)

            try {
                prevOnGround[player.uuid] = player.isOnGround
            } catch (_: Exception) {}
        }
    }

    /**
     * Reads weight contribution from a single inventory slot.
     * @return Pair(contribution, hasEntityOver5Blocks)
     */
    private fun readBasketContribution(stack: ItemStack): Pair<Int, Boolean> {
        // Fast-path: not a basket, empty, or no NBT → skip immediately
        if (stack.item !== ModItems.BASKET || stack.isEmpty) return Pair(0, false)
        val nbt = stack.nbt ?: return Pair(0, false)
        if (!nbt.contains("StoredEntity")) return Pair(0, false)

        val entNbt = nbt.getCompound("StoredEntity")
        val width = entNbt.getDouble("ColliderWidth")
        val height = entNbt.getDouble("ColliderHeight")

        return when {
            width > 5.0 || height > 5.0 -> Pair(0, true)  // >5: contribution irrelevant, flag triggers 16
            width > 2.0 || height > 2.0 -> Pair(4, false)
            width > 1.0 || height > 1.0 -> Pair(2, false)
            else -> Pair(0, false)
        }
    }

    private fun applyBasketEffects(player: PlayerEntity, totalAmplifier: Int) {
        // Slowness effect: refreshed every tick while encumbered
        val amplifier = totalAmplifier - 1
        player.addStatusEffect(
            StatusEffectInstance(StatusEffects.SLOWNESS, 40, amplifier, false, false)
        )

        // Prevent sprinting
        try {
            if (player.isSprinting) player.setSprinting(false)
        } catch (_: Exception) {}

        // Extra exhaustion on jump
        try {
            val id = player.uuid
            val wasOnGround = prevOnGround[id] ?: player.isOnGround
            val isOnGround = player.isOnGround
            if (wasOnGround && !isOnGround) {
                player.addExhaustion(0.6f)
            }
            prevOnGround[id] = isOnGround
        } catch (_: Exception) {}

        // Heavy pressure damage (triggers at Slowness VI, i.e. totalAmplifier >= 6)
        if (totalAmplifier >= 6) {
            applyHeavyPressureDamage(player)
        }
    }

    private fun applyHeavyPressureDamage(player: PlayerEntity) {
        val id = player.uuid
        val tickCount = (heavyPressureTickCounter[id] ?: 0) + 1

        if (tickCount < HEAVY_PRESSURE_INTERVAL) {
            heavyPressureTickCounter[id] = tickCount
            return
        }

        heavyPressureTickCounter[id] = 0

        try {
            val damageTypeRegistry = player.world.registryManager.get(RegistryKeys.DAMAGE_TYPE)
            val crushingKey = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, ModDamageTypes.CRUSHING_ID)
            val crushingEntry = damageTypeRegistry.getEntry(crushingKey)
            if (crushingEntry.isPresent) {
                player.damage(DamageSource(crushingEntry.get()), HEAVY_PRESSURE_DAMAGE)
            }
        } catch (_: Exception) {}
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
        heavyPressureTickCounter.remove(playerId)
    }
}
