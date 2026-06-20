package com.hydroceder.hgbg

import com.hydroceder.hgbg.config.ModConfig
import com.hydroceder.hgbg.event.ModEnchantments
import com.hydroceder.hgbg.event.PlayerLifecycleHandler
import com.hydroceder.hgbg.event.ServerTickHandler
import com.hydroceder.hgbg.event.CombatHandler
import com.hydroceder.hgbg.event.BonemealHandler
import com.hydroceder.hgbg.seasoning.ModSeasonings
import com.hydroceder.hgbg.block.ModBlocks
import com.hydroceder.hgbg.block.ModBlockEntityTypes
import com.hydroceder.hgbg.recipe.pan_cooking.PanCookingRecipe
import com.hydroceder.hgbg.recipe.pan_cooking.PanCookingRecipeManager
import com.hydroceder.hgbg.recipe.ModRecipeTypes
import com.hydroceder.hgbg.item.ModItems
import com.hydroceder.hgbg.command.HgbgCommand
import com.hydroceder.hgbg.structure.ModFeatures
import com.hydroceder.hgbg.entity.ModEntities
import com.hydroceder.hgbg.sound.ModSounds
import com.hydroceder.hgbg.item.ModItemGroups
import com.hydroceder.hgbg.effect.ModEffects
import com.hydroceder.hgbg.damage.ModDamageTypes
import com.hydroceder.hgbg.villager.ModVillagerTrades
import net.minecraft.loot.LootTables
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemGroups
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import com.hydroceder.hgbg.item.tool.BasketItem
import net.minecraft.world.World
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import org.slf4j.LoggerFactory
import java.util.UUID

object HungerBegone : ModInitializer {
    private val logger = LoggerFactory.getLogger("hunger-begone")

    override fun onInitialize() {

        ModConfig.load()

        ModEntities.register()

        ModSounds.register()

        ModFeatures.register()

        ModItems.register()

        ModItemGroups.register()

        ModBlocks.register()

        ModBlockEntityTypes.register()

        ModRecipeTypes.register()

        ModEffects.register()

        ModDamageTypes.register()

        registerAdvancementCriteria()

        ModEnchantments.register()

        PlayerLifecycleHandler.register()
        ServerTickHandler.register()
        CombatHandler.register()
        BonemealHandler.register()

        com.hydroceder.hgbg.event.CoinMachineDamageHandler.register()
        logger.info("Coin machine damage handler registered!")

        com.hydroceder.hgbg.event.CoinMachineMusicTicker.register()
        logger.info("Coin machine music ticker registered!")

        com.hydroceder.hgbg.event.CalmnessEventHandler.register()
        logger.info("Calmness effect handler registered!")

        com.hydroceder.hgbg.event.PlayerDisconnectHandler.register()
        logger.info("Player disconnect handler registered!")

        registerRecipeSyncListener()

        registerCommands()

        registerLootTableModifications()

        ModSeasonings.register()

        ModVillagerTrades.register()

        registerEntityUseCallback()

        registerVanillaCreativeTabs()
    }

    private fun registerEntityUseCallback() {
        UseEntityCallback.EVENT.register(UseEntityCallback { player, world, hand, entity, hitResult ->
            // only run on server
            if (world.isClient) return@UseEntityCallback ActionResult.PASS

            // only care about living entities and sneaking players
            if (!player.isSneaking) return@UseEntityCallback ActionResult.PASS
            if (entity !is LivingEntity) return@UseEntityCallback ActionResult.PASS

            val mainHand = player.mainHandStack
            val offHand = player.offHandStack

            // prefer capturing when holding a basket in either hand
            val handStack = if (mainHand.item === com.hydroceder.hgbg.item.ModItems.BASKET) mainHand else if (offHand.item === com.hydroceder.hgbg.item.ModItems.BASKET) offHand else null
            if (handStack == null) return@UseEntityCallback ActionResult.PASS

            // Respect cooldown: no capturing during cooldown
            if (player.itemCooldownManager.isCoolingDown(handStack.item)) {
                return@UseEntityCallback ActionResult.FAIL
            }

            // if other hand is empty and basket has stored entity, placement has priority - skip capture here
            val otherHandEmpty = (if (handStack === mainHand) offHand.isEmpty else mainHand.isEmpty)
            // If player is sneaking and holding basket, capture the entity
            val stack = handStack

            // Delegate capture behavior to BasketItem.useOnEntity equivalent method
            try {
                val worldServer = world as ServerWorld
                val basket = stack
                // call the existing capture helper on server
                if (com.hydroceder.hgbg.item.tool.BasketItem.attemptCaptureEntity(basket, player, entity as LivingEntity, hand)) {
                    return@UseEntityCallback ActionResult.SUCCESS
                }
            } catch (ex: Exception) {
                logger.error("Error while attempting basket capture", ex)
            }

            return@UseEntityCallback ActionResult.PASS
        })
        logger.info("Entity use callback registered for basket capture")
    }

    private fun registerVanillaCreativeTabs() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register { content ->
            content.addAfter(
                net.minecraft.item.Items.BIRCH_BUTTON,
                ModBlocks.COCONUT_LOG,
                ModBlocks.PALM_PLANKS,
                ModBlocks.PALM_SLAB,
                ModBlocks.PALM_STAIRS,
                ModBlocks.PALM_FENCE,
                ModBlocks.PALM_FENCE_GATE,
                ModBlocks.PALM_PRESSURE_PLATE,
                ModBlocks.PALM_BUTTON
            )
        }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register { content ->
            content.addAfter(
                net.minecraft.item.Items.BIRCH_SAPLING,
                ModBlocks.COCONUT_SAPLING
            )
            content.addAfter(
                net.minecraft.item.Items.BIRCH_LEAVES,
                ModBlocks.COCONUT_LEAVES
            )
        }
        logger.info("Palm wood blocks added to vanilla creative tabs!")
    }

    private fun registerCommands() {
        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            HgbgCommand.register(dispatcher)
        }
        logger.info("HGBG commands registered!")
    }

    private fun registerRecipeSyncListener() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register { player, joined ->
            PanCookingRecipeManager.loadRecipesFromServer(player.server)
            logger.info("Pan cooking recipes loaded: {}", PanCookingRecipeManager.getAllRecipes().size)
            com.hydroceder.hgbg.recipe.stew_pot_cooking.StewPotRecipeManager.loadRecipesFromServer(player.server)
            logger.info("Stew pot cooking recipes loaded: {}", com.hydroceder.hgbg.recipe.stew_pot_cooking.StewPotRecipeManager.getAllRecipes().size)
        }

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            PanCookingRecipeManager.loadRecipesFromServer(server)
            logger.info("Pan cooking recipes loaded: {}", PanCookingRecipeManager.getAllRecipes().size)
            com.hydroceder.hgbg.recipe.stew_pot_cooking.StewPotRecipeManager.loadRecipesFromServer(server)
            logger.info("Stew pot cooking recipes loaded: {}", com.hydroceder.hgbg.recipe.stew_pot_cooking.StewPotRecipeManager.getAllRecipes().size)
        }
    }

    private fun registerLootTableModifications() {
        LootTableEvents.MODIFY.register { resourceManager, manager, id, tableBuilder, source ->
            if (id == LootTables.FISHING_GAMEPLAY) {
                tableBuilder.modifyPools { pool ->
                    pool.with(ItemEntry.builder(ModItems.RAW_SHRIMP)
                        .weight(50)
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0f))))
                }

                logger.info("Added raw shrimp to fishing loot table!")
            }
        }
    }

    private fun registerAdvancementCriteria() {
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.HomelandDirtTrigger.getInstance())
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.DistanceTrigger.getInstance())
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.BonemealOakLeavesTrigger.getInstance())
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.ObtainLemonTrigger.getInstance())
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.BonemealCrimsonTrigger.getInstance())
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.PlantEggplantTrigger.getInstance())
    }

    fun cleanupPlayerData(playerId: UUID) {
        PlayerLifecycleHandler.cleanupPlayerData(playerId)
        ServerTickHandler.cleanupPlayerData(playerId)
    }
}
