package com.hydroceder.hgbg.event

import com.hydroceder.hgbg.item.ModItems
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import kotlin.random.Random
import net.minecraft.world.World
import net.fabricmc.fabric.api.event.player.UseBlockCallback

object BonemealHandler {

    fun register() {
        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
            val heldStack = player.getStackInHand(hand)
            val blockState = world.getBlockState(hitResult.blockPos)
            val blockId = Registries.BLOCK.getId(blockState.block)

            if (heldStack.isOf(Items.BONE_MEAL)) {
                when (blockId.path) {
                    "azalea", "flowering_azalea" -> handleAzaleaBonemeal(world, hitResult.blockPos, player)
                    "oak_leaves" -> handleOakLeavesBonemeal(world, heldStack, hitResult.blockPos, player)
                    "birch_leaves" -> handleBirchLeavesBonemeal(world, heldStack, hitResult.blockPos, player)
                    "weeping_vines_plant" -> handleWeepingVinesBonemeal(world, heldStack, hitResult.blockPos, player)
                    else -> {}
                }
            }
            return@UseBlockCallback ActionResult.PASS
        })
    }

    private fun handleAzaleaBonemeal(world: World, pos: net.minecraft.util.math.BlockPos, player: net.minecraft.entity.player.PlayerEntity) {
        if (world.isClient) {
            spawnHappyVillagerParticles(world, pos)
        }

        if (!world.isClient) {
            val lemonCount = Random.nextInt(1, 3)
            val lemonStack = ItemStack(ModItems.LEMON, lemonCount)
            ItemEntity(world, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, lemonStack).apply {
                setToDefaultPickupDelay()
                world.spawnEntity(this)
            }

            com.hydroceder.hgbg.advancement.ObtainLemonTrigger.getInstance().trigger(player as ServerPlayerEntity)
        }
    }

    private fun handleOakLeavesBonemeal(world: World, heldStack: ItemStack, pos: net.minecraft.util.math.BlockPos, player: net.minecraft.entity.player.PlayerEntity): ActionResult {
        if (!world.isClient && !player.abilities.creativeMode) {
            heldStack.decrement(1)
        }

        if (world.isClient) {
            spawnHappyVillagerParticles(world, pos)
        }

        if (!world.isClient) {
            val appleStack = ItemStack(Items.APPLE, 1)
            ItemEntity(world, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, appleStack).apply {
                setToDefaultPickupDelay()
                world.spawnEntity(this)
            }

            com.hydroceder.hgbg.advancement.BonemealOakLeavesTrigger.getInstance().trigger(player as ServerPlayerEntity)
        }

        return ActionResult.SUCCESS
    }

    private fun handleBirchLeavesBonemeal(world: World, heldStack: ItemStack, pos: net.minecraft.util.math.BlockPos, player: net.minecraft.entity.player.PlayerEntity): ActionResult {
        if (!world.isClient && !player.abilities.creativeMode) {
            heldStack.decrement(1)
        }

        if (world.isClient) {
            spawnHappyVillagerParticles(world, pos)
        }

        if (!world.isClient) {
            val lemonCount = Random.nextInt(1, 3)
            val lemonStack = ItemStack(ModItems.LEMON, lemonCount)
            ItemEntity(world, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, lemonStack).apply {
                setToDefaultPickupDelay()
                world.spawnEntity(this)
            }

            com.hydroceder.hgbg.advancement.ObtainLemonTrigger.getInstance().trigger(player as ServerPlayerEntity)
        }

        return ActionResult.SUCCESS
    }

    private fun handleWeepingVinesBonemeal(world: World, heldStack: ItemStack, pos: net.minecraft.util.math.BlockPos, player: net.minecraft.entity.player.PlayerEntity): ActionResult {
        if (world.isClient) {
            spawnHappyVillagerParticles(world, pos)
        }

        if (!world.isClient) {
            val crimsonFungusCount = Random.nextInt(2, 4)
            val crimsonFungusStack = ItemStack(Items.CRIMSON_FUNGUS, crimsonFungusCount)
            ItemEntity(world, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, crimsonFungusStack).apply {
                setToDefaultPickupDelay()
                world.spawnEntity(this)
            }

            com.hydroceder.hgbg.advancement.BonemealCrimsonTrigger.getInstance().trigger(player as ServerPlayerEntity)

            if (!player.abilities.creativeMode) {
                heldStack.decrement(1)
            }
        }

        return ActionResult.SUCCESS
    }

    private fun spawnHappyVillagerParticles(world: World, pos: net.minecraft.util.math.BlockPos) {
        for (i in 0..15) {
            val x = pos.x + 0.5 + (Random.nextDouble() - 0.5) * 1.0
            val y = pos.y + 0.5 + (Random.nextDouble() - 0.5) * 1.0
            val z = pos.z + 0.5 + (Random.nextDouble() - 0.5) * 1.0
            world.addParticle(
                ParticleTypes.HAPPY_VILLAGER,
                x, y, z,
                (Random.nextDouble() - 0.5) * 0.1,
                Random.nextDouble() * 0.1,
                (Random.nextDouble() - 0.5) * 0.1
            )
        }
    }
}
