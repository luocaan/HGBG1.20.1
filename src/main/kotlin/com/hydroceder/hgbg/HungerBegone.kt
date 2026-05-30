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
import net.minecraft.loot.LootTables
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
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
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.RunTrigger.getInstance())
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
