package com.hydroceder.hgbg.event

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.BambooBlock
import net.minecraft.block.BambooSaplingBlock
import net.minecraft.block.CactusBlock
import net.minecraft.block.CaveVinesBodyBlock
import net.minecraft.block.CaveVinesHeadBlock
import net.minecraft.block.CocoaBlock
import net.minecraft.block.CropBlock
import net.minecraft.block.NetherWartBlock
import net.minecraft.block.PitcherCropBlock
import net.minecraft.block.SugarCaneBlock
import net.minecraft.block.SweetBerryBushBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import org.slf4j.LoggerFactory

object CombatHandler {
    private val logger = LoggerFactory.getLogger("hunger-begone")

    fun register() {
        registerKillEvent()
        registerBlockBreakEvent()
    }

    private fun registerKillEvent() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(ServerEntityCombatEvents.AfterKilledOtherEntity { world, entity, killedEntity ->
            if (entity is PlayerEntity) {
                if (ModEnchantments.hasNourishmentEnchantment(entity)) {
                    val maxHealth = if (killedEntity is LivingEntity) {
                        killedEntity.maxHealth
                    } else {
                        0.0
                    }

                    val foodToAdd = maxHealth.toInt()

                    if (foodToAdd > 0) {
                        entity.hungerManager.add(foodToAdd, 1.0f)
                        logger.info("Player {} gained {} hunger from killing {}", entity.name.string, foodToAdd, killedEntity.type.translationKey)
                    }
                }
            }
        })
    }

    private fun registerBlockBreakEvent() {
        PlayerBlockBreakEvents.AFTER.register(PlayerBlockBreakEvents.After { world, player, pos, state, blockEntity ->
            if (!world.isClient) {
                val fieldHarvesterLevel = ModEnchantments.getFieldHarvesterEnchantmentLevel(player)
                if (fieldHarvesterLevel > 0) {
                    if (isCropBlock(state)) {
                        when (fieldHarvesterLevel) {
                            1 -> {
                                if (world.random.nextFloat() < 0.5f) {
                                    dropExtraItems(world, pos, state)
                                }
                            }
                            2 -> {
                                dropExtraItems(world, pos, state)
                            }
                            3 -> {
                                dropExtraItems(world, pos, state)
                                if (world.random.nextFloat() < 0.5f) {
                                    dropItem(world, pos, Items.BONE_MEAL.defaultStack)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    fun isCropBlock(state: BlockState): Boolean {
        val block = state.block
        return block is CropBlock ||
               block is NetherWartBlock ||
               block is CocoaBlock ||
               block is SugarCaneBlock ||
               block is CactusBlock ||
               block is BambooBlock ||
               block is BambooSaplingBlock ||
               block is SweetBerryBushBlock ||
               block is CaveVinesBodyBlock ||
               block is CaveVinesHeadBlock ||
               block is PitcherCropBlock
    }

    private fun dropExtraItems(world: World, pos: BlockPos, state: BlockState) {
        val drops = Block.getDroppedStacks(state, world.server!!.overworld, pos, null)
        for (drop in drops) {
            if (!drop.isEmpty) {
                val extraDrop = drop.copy()
                Block.dropStack(world, pos, extraDrop)
            }
        }
    }

    private fun dropItem(world: World, pos: BlockPos, stack: ItemStack) {
        Block.dropStack(world, pos, stack)
    }
}
