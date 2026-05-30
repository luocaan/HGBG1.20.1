package com.hydroceder.hgbg.event

import com.hydroceder.hgbg.enchantment.EnhancedInsecticideEnchantment
import com.hydroceder.hgbg.enchantment.EnthusiasmEnchantment
import com.hydroceder.hgbg.enchantment.FieldHarvesterEnchantment
import com.hydroceder.hgbg.enchantment.NozzleImprovementEnchantment
import com.hydroceder.hgbg.enchantment.NourishmentEnchantment
import com.hydroceder.hgbg.enchantment.NutritionEnchantment
import com.hydroceder.hgbg.enchantment.RecipeImprovementEnchantment
import com.hydroceder.hgbg.enchantment.SpeedEnchantment
import com.hydroceder.hgbg.enchantment.WarmthEnchantment
import net.minecraft.block.Blocks
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry
import org.slf4j.LoggerFactory

object ModEnchantments {
    private val logger = LoggerFactory.getLogger("hunger-begone")

    fun register() {
        NourishmentEnchantment.INSTANCE = NourishmentEnchantment()
        Registry.register(Registries.ENCHANTMENT, NourishmentEnchantment.ID, NourishmentEnchantment.INSTANCE)
        logger.info("Nourishment enchantment registered successfully!")

        NutritionEnchantment.INSTANCE = NutritionEnchantment()
        Registry.register(Registries.ENCHANTMENT, NutritionEnchantment.ID, NutritionEnchantment.INSTANCE)
        logger.info("Nutrition enchantment registered successfully!")

        SpeedEnchantment.INSTANCE = SpeedEnchantment()
        Registry.register(Registries.ENCHANTMENT, SpeedEnchantment.ID, SpeedEnchantment.INSTANCE)
        logger.info("Speed enchantment registered successfully!")

        WarmthEnchantment.INSTANCE = WarmthEnchantment()
        Registry.register(Registries.ENCHANTMENT, WarmthEnchantment.ID, WarmthEnchantment.INSTANCE)
        logger.info("Warmth enchantment registered successfully!")

        EnthusiasmEnchantment.INSTANCE = EnthusiasmEnchantment()
        Registry.register(Registries.ENCHANTMENT, EnthusiasmEnchantment.ID, EnthusiasmEnchantment.INSTANCE)
        logger.info("Enthusiasm enchantment registered successfully!")

        FieldHarvesterEnchantment.INSTANCE = FieldHarvesterEnchantment()
        Registry.register(Registries.ENCHANTMENT, FieldHarvesterEnchantment.ID, FieldHarvesterEnchantment.INSTANCE)
        logger.info("Field Harvester enchantment registered successfully!")

        EnhancedInsecticideEnchantment.INSTANCE = EnhancedInsecticideEnchantment()
        Registry.register(Registries.ENCHANTMENT, EnhancedInsecticideEnchantment.ID, EnhancedInsecticideEnchantment.INSTANCE)
        logger.info("Enhanced Insecticide enchantment registered successfully!")

        FlammableBlockRegistry.getDefaultInstance().add(Blocks.COBWEB, 100, 60)
        logger.info("Cobweb registered as flammable block!")

        NozzleImprovementEnchantment.INSTANCE = NozzleImprovementEnchantment()
        Registry.register(Registries.ENCHANTMENT, NozzleImprovementEnchantment.ID, NozzleImprovementEnchantment.INSTANCE)
        logger.info("Nozzle Improvement enchantment registered successfully!")

        RecipeImprovementEnchantment.INSTANCE = RecipeImprovementEnchantment()
        Registry.register(Registries.ENCHANTMENT, RecipeImprovementEnchantment.ID, RecipeImprovementEnchantment.INSTANCE)
        logger.info("Recipe Improvement enchantment registered successfully!")
    }

    fun hasNourishmentEnchantment(player: PlayerEntity): Boolean {
        for (slot in EquipmentSlot.values()) {
            val stack = player.getEquippedStack(slot)
            if (hasNourishmentEnchantment(stack)) {
                return true
            }
        }
        return false
    }

    fun hasNourishmentEnchantment(stack: ItemStack): Boolean {
        return EnchantmentHelper.getLevel(NourishmentEnchantment.INSTANCE, stack) > 0
    }

    fun hasNourishmentEnchantmentLevel2(player: PlayerEntity): Boolean {
        for (slot in EquipmentSlot.values()) {
            val stack = player.getEquippedStack(slot)
            if (EnchantmentHelper.getLevel(NourishmentEnchantment.INSTANCE, stack) >= 2) {
                return true
            }
        }
        return false
    }

    fun getSpeedEnchantmentLevel(player: PlayerEntity): Int {
        val boots = player.getEquippedStack(EquipmentSlot.FEET)
        return EnchantmentHelper.getLevel(SpeedEnchantment.INSTANCE, boots)
    }

    fun getFieldHarvesterEnchantmentLevel(player: PlayerEntity): Int {
        var maxLevel = 0
        for (slot in EquipmentSlot.values()) {
            val stack = player.getEquippedStack(slot)
            val level = EnchantmentHelper.getLevel(FieldHarvesterEnchantment.INSTANCE, stack)
            if (level > maxLevel) {
                maxLevel = level
            }
        }
        return maxLevel
    }
}
