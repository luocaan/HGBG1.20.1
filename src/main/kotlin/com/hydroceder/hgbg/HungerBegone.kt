package com.hydroceder.hgbg

import com.hydroceder.hgbg.enchantment.NourishmentEnchantment
import com.hydroceder.hgbg.enchantment.NutritionEnchantment
import com.hydroceder.hgbg.enchantment.SpeedEnchantment
import com.hydroceder.hgbg.enchantment.WarmthEnchantment
import com.hydroceder.hgbg.enchantment.EnthusiasmEnchantment
import com.hydroceder.hgbg.enchantment.FieldHarvesterEnchantment
import com.hydroceder.hgbg.seasoning.SeasoningRegistry
import com.hydroceder.hgbg.seasoning.SimpleSeasoning
import com.hydroceder.hgbg.effect.ModEffects
import com.hydroceder.hgbg.effect.HomesicknessEffect
import com.hydroceder.hgbg.util.PotionEffectRemover
import com.hydroceder.hgbg.util.SaturationTracker
import com.hydroceder.hgbg.block.ModBlocks
import com.hydroceder.hgbg.block.ModBlockEntityTypes
import com.hydroceder.hgbg.recipe.pan_cooking.CookingRecipeCache
import com.hydroceder.hgbg.recipe.pan_cooking.PanCookingRecipe
import com.hydroceder.hgbg.recipe.pan_cooking.PanCookingRecipeManager
import com.hydroceder.hgbg.recipe.ModRecipeTypes
import com.hydroceder.hgbg.item.ModItems
import com.hydroceder.hgbg.item.material.HomelandDirtItem
import com.hydroceder.hgbg.command.HgbgCommand
import com.hydroceder.hgbg.structure.ModFeatures
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTables
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundEvents
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.random.Random

object HungerBegone : ModInitializer {
    private val logger = LoggerFactory.getLogger("hunger-begone")
    
    // 饱食疾行修饰符UUID
    private val SPEED_MODIFIER_UUID = java.util.UUID.fromString("35023419-1981-0114-5140-350234191981")

	override fun onInitialize() {

        
        // 加载配置文件
        com.hydroceder.hgbg.config.ModConfig.load()
        
        // 注册实体
        com.hydroceder.hgbg.entity.ModEntities.register()
        
        // 注册音效
        com.hydroceder.hgbg.sound.ModSounds.register()
        
        // 注册地物（Feature）
        com.hydroceder.hgbg.structure.ModFeatures.register()
        
        // 注册物品
        com.hydroceder.hgbg.item.ModItems.register()
        
        // 注册物品组
        com.hydroceder.hgbg.item.ModItemGroups.register()
        
        // 注册方块
        ModBlocks.register()
        
        // 注册方块实体
        ModBlockEntityTypes.register()
        
        // 注册配方类型
        ModRecipeTypes.register()
        
        // 注册药水效果
        ModEffects.register()
        
        // 注册进度触发器
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.HomelandDirtTrigger.getInstance())
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.DistanceTrigger.getInstance())
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.RunTrigger.getInstance())
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.BonemealOakLeavesTrigger.getInstance())
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.ObtainLemonTrigger.getInstance())
        net.minecraft.advancement.criterion.Criteria.register(com.hydroceder.hgbg.advancement.BonemealCrimsonTrigger.getInstance())
        
        // 注册滋养附魔
        registerEnchantments()
        
        // 注册事件监听器
        registerEvents()

        // 注册投币机伤害事件
        com.hydroceder.hgbg.event.CoinMachineDamageHandler.register()
        logger.info("Coin machine damage handler registered!")
        
        // 注册投币机音乐播放tick事件
        com.hydroceder.hgbg.event.CoinMachineMusicTicker.register()
        logger.info("Coin machine music ticker registered!")

        // 注册冷静效果事件处理器
        com.hydroceder.hgbg.event.CalmnessEventHandler.register()
        logger.info("Calmness effect handler registered!")
        
        // 注册玩家数据清理处理器（统一管理所有静态Map数据清理）
        com.hydroceder.hgbg.event.PlayerDisconnectHandler.register()
        logger.info("Player disconnect handler registered!")
        
        // 注册配方同步事件监听器
        registerRecipeSyncListener()
        
        // 注册命令
        registerCommands()
        
        // 注册战利品表修改
        registerLootTableModifications()
        
        // 注册调味料
        registerSeasonings()
	}
    
    /**
     * 注册命令
     */
    private fun registerCommands() {
        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            HgbgCommand.register(dispatcher)
        }
        logger.info("HGBG commands registered!")
    }
    
    private fun registerEnchantments() {
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
    }
    
    private fun registerEvents() {
        // 注册玩家首次进入游戏事件，发放故乡土壤
        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEvents.Join {
            handler, sender, server ->
            val player = handler.player
            
            // 检查是否启用故乡土壤给予功能
            if (com.hydroceder.hgbg.config.ModConfig.isGiveHomelandDirtEnabled()) {
                // 检查玩家是否已有故乡土壤
                var hasHomelandDirt = false
                for (stack in player.inventory.main) {
                    if (stack.item == ModItems.HOMELAND_DIRT) {
                        hasHomelandDirt = true
                        break
                    }
                }
                
                // 检查副手
                if (!hasHomelandDirt && player.offHandStack.item == ModItems.HOMELAND_DIRT) {
                    hasHomelandDirt = true
                }
                
                // 如果没有，发放故乡土壤
                if (!hasHomelandDirt) {
                    val homelandDirt = net.minecraft.item.ItemStack(ModItems.HOMELAND_DIRT, 1)
                    if (!player.inventory.insertStack(homelandDirt)) {
                        // 如果背包满了，就丢在地上
                        player.dropItem(homelandDirt, false)
                    }
                    logger.info("Gave homeland dirt to new player: {}", player.name.string)
                }
            }
        })
        
        // 注册死亡事件，触发故乡土壤效果
        ServerLivingEntityEvents.ALLOW_DEATH.register(ServerLivingEntityEvents.AllowDeath {
            entity, damageSource, damageAmount ->
            if (entity is PlayerEntity) {
                // 尝试使用故乡土壤
                if (HomelandDirtItem.tryUseHomelandDirt(entity, entity.world)) {
                    // 阻止死亡
                    return@AllowDeath false
                }
            }
            // 允许死亡
            return@AllowDeath true
        })
        
        // 注册伤害事件，处理归心效果的伤害重新计算
        val processingDamage = java.util.HashSet<UUID>()
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(ServerLivingEntityEvents.AllowDamage {
            entity, damageSource, amount ->
            if (entity is PlayerEntity && entity.hasStatusEffect(HomesicknessEffect.INSTANCE)) {
                val playerId = entity.uuid
                // 检查是否已经在处理伤害，避免递归
                if (processingDamage.contains(playerId)) {
                    return@AllowDamage true
                }
                
                // 检查伤害是否大于玩家最大生命值
                val maxHealth = entity.maxHealth
                if (amount > maxHealth) {
                    // 将伤害降至最大生命值的一半
                    val reducedDamage = maxHealth / 2.0f
                    try {
                        processingDamage.add(playerId)
                        // 先移除归心效果
                        val hadEffect = entity.hasStatusEffect(HomesicknessEffect.INSTANCE)
                        entity.removeStatusEffect(HomesicknessEffect.INSTANCE)
                        // 应用减少后的伤害
                        entity.damage(damageSource, reducedDamage)
                        // 如果之前有效果，重新添加
                        if (hadEffect) {
                            entity.addStatusEffect(StatusEffectInstance(HomesicknessEffect.INSTANCE, 100, 0, false, false))
                        }
                    } finally {
                        processingDamage.remove(playerId)
                    }
                    // 阻止原始伤害
                    return@AllowDamage false
                }
            }
            // 允许原始伤害
            return@AllowDamage true
        })
        
        // 注册实体战斗事件监听器
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(ServerEntityCombatEvents.AfterKilledOtherEntity {
            world, entity, killedEntity ->
            // 检查杀死实体的是否是玩家
            if (entity is PlayerEntity) {
                // 检查玩家装备是否有滋养附魔
                if (hasNourishmentEnchantment(entity)) {
                    // 计算被杀死实体的最大生命值
                    val maxHealth = if (killedEntity is LivingEntity) {
                        killedEntity.maxHealth
                    } else {
                        0.0
                    }
                    
                    // 转换为饥饿值（取整数部分）
                    val foodToAdd = maxHealth.toInt()
                    
                    if (foodToAdd > 0) {
                        // 增加饥饿值和饱和度
                        entity.hungerManager.add(foodToAdd, 1.0f)
                        logger.info("Player {} gained {} hunger from killing {}", entity.name.string, foodToAdd, killedEntity.type.translationKey)
                    }
                }
            }
        })
        
        // 注册服务器tick事件监听器，用于检测饱和度和移除负面效果，以及处理饱食疾行附魔的速度修饰符
        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick {
            server ->
            // 遍历所有玩家
            for (player in server.playerManager.playerList) {
                // 检查玩家装备是否有II级滋养附魔
                if (hasNourishmentEnchantmentLevel2(player)) {
                    // 更新玩家的饱和度状态
                    SaturationTracker.update(player)
                    
                    // 检查饱和度是否满且持续时间超过5秒
                    if (SaturationTracker.isSaturationFullForLongEnough(player)) {
                        // 尝试移除一个负面效果
                        if (PotionEffectRemover.removeNegativeEffect(player)) {
                            // 移除效果后重置饱和度状态
                            SaturationTracker.reset(player)
                        }
                    }
                }
                
                // 处理饱食疾行附魔的速度修饰符
                // 检查玩家是否穿着带有饱食疾行附魔的鞋
                val speedLevel = getSpeedEnchantmentLevel(player)
                
                // 检查玩家的饱和度是否＞0
                val hasSaturation = player.hungerManager.saturationLevel > 0.0f
                
                // 检查玩家是否正在疾跑
                val isSprinting = player.isSprinting
                
                // 获取速度属性
                val speedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                
                if (speedAttribute != null) {
                    // 移除现有的修饰符
                    speedAttribute.removeModifier(SPEED_MODIFIER_UUID)
                    
                    // 如果满足条件，添加速度修饰符
                    if (speedLevel > 0 && hasSaturation && isSprinting) {
                        val speedBonus = when (speedLevel) {
                            1 -> 0.2 // I级增加20%
                            2 -> 0.3 // II级增加30%
                            3 -> 0.4 // III级增加40%
                            else -> 0.0
                        }
                        
                        val modifier = EntityAttributeModifier(
                            SPEED_MODIFIER_UUID,
                            "Satiated Sprint speed bonus",
                            speedBonus,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                        )
                        
                        speedAttribute.addPersistentModifier(modifier)
                    }
                }
                
                // 处理田野收获者附魔的缓降效果
                val fieldHarvesterLevel = getFieldHarvesterEnchantmentLevel(player)
                if (fieldHarvesterLevel > 0 && isNearFarmland(player.world, player.blockPos)) {
                    // 为玩家添加缓降效果（持续1秒，等级为0）
                    player.addStatusEffect(StatusEffectInstance(StatusEffects.SLOW_FALLING, 60, 0, false, false))
                }
                
                // 处理归心效果
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
                
                // 检查坐标是否任意项大于1000，触发饱腹远行进度
                val x = Math.abs(player.x).toInt()
                val y = Math.abs(player.y).toInt()
                val z = Math.abs(player.z).toInt()
                
                if (x > 1000 || y > 1000 || z > 1000) {
                    // 触发距离进度
                    com.hydroceder.hgbg.advancement.DistanceTrigger.getInstance().trigger(player as net.minecraft.server.network.ServerPlayerEntity)
                }
                
                // 检查玩家是否正在疾跑，触发自由奔驰进度
                if (player.isSprinting) {
                    // 触发奔跑进度
                    com.hydroceder.hgbg.advancement.RunTrigger.getInstance().trigger(player as net.minecraft.server.network.ServerPlayerEntity)
                }
                
                if (hasHomelandDirt) {
                    // 检查是否不在主世界，或者坐标任意项大于30000
                    val isNotOverworld = player.world.registryKey != net.minecraft.world.World.OVERWORLD
                    if (isNotOverworld || x > 30000 || y > 30000 || z > 30000) {
                        // 给予归心效果（持续5秒，等级0）
                        player.addStatusEffect(StatusEffectInstance(HomesicknessEffect.INSTANCE, 100, 0, false, false))
                    }
                }
            }
        })
        
        // 注册方块破坏事件监听器，用于处理作物收获效果
        PlayerBlockBreakEvents.AFTER.register(PlayerBlockBreakEvents.After { world, player, pos, state, blockEntity ->
            if (!world.isClient) {
                val fieldHarvesterLevel = getFieldHarvesterEnchantmentLevel(player)
                if (fieldHarvesterLevel > 0) {
                    // 检查是否是作物
                    if (isCropBlock(state)) {
                        when (fieldHarvesterLevel) {
                            1 -> {
                                // I级：50%概率翻倍
                                if (world.random.nextFloat() < 0.5f) {
                                    dropExtraItems(world, pos, state)
                                }
                            }
                            2 -> {
                                // II级：必定翻倍
                                dropExtraItems(world, pos, state)
                            }
                            3 -> {
                                // III级：必定翻倍，50%概率掉落骨粉
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
        
        // 注册骨粉催熟事件
        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, hitResult ->
            val heldStack = player.getStackInHand(hand)
            val blockState = world.getBlockState(hitResult.blockPos)
            val blockId = Registries.BLOCK.getId(blockState.block)
            
            // 检查是否使用骨粉
            if (heldStack.isOf(Items.BONE_MEAL)) {
                // 检查是否是杜鹃花丛或盛开的杜鹃花丛
                if (blockId.path == "azalea" || blockId.path == "flowering_azalea") {
                    // 播放粒子效果
                    if (world.isClient) {
                        val pos = hitResult.blockPos
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
                    
                    // 掉落1-2个柠檬（仅服务器端）
                    if (!world.isClient) {
                        val lemonCount = Random.nextInt(1, 3)
                        val lemonStack = ItemStack(ModItems.LEMON, lemonCount)
                        net.minecraft.entity.ItemEntity(world, hitResult.blockPos.x + 0.5, hitResult.blockPos.y + 0.5, hitResult.blockPos.z + 0.5, lemonStack).apply {
                            setToDefaultPickupDelay()
                            world.spawnEntity(this)
                        }
                        
                        // 触发柠檬树进度
                        com.hydroceder.hgbg.advancement.ObtainLemonTrigger.getInstance().trigger(player as net.minecraft.server.network.ServerPlayerEntity)
                    }
                    
                    // 返回PASS让原版催熟逻辑继续执行
                    return@UseBlockCallback net.minecraft.util.ActionResult.PASS
                }
                
                // 检查是否是橡木树叶
                if (blockId.path == "oak_leaves") {
                    // 消耗骨粉（仅服务器端）
                    if (!world.isClient && !player.abilities.creativeMode) {
                        heldStack.decrement(1)
                    }
                    
                    // 播放粒子效果
                    if (world.isClient) {
                        val pos = hitResult.blockPos
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
                    
                    // 掉落1个苹果（仅服务器端）
                    if (!world.isClient) {
                        val appleStack = ItemStack(Items.APPLE, 1)
                        net.minecraft.entity.ItemEntity(world, hitResult.blockPos.x + 0.5, hitResult.blockPos.y + 0.5, hitResult.blockPos.z + 0.5, appleStack).apply {
                            setToDefaultPickupDelay()
                            world.spawnEntity(this)
                        }
                        
                        // 触发苹果树进度
                        com.hydroceder.hgbg.advancement.BonemealOakLeavesTrigger.getInstance().trigger(player as net.minecraft.server.network.ServerPlayerEntity)
                    }
                    
                    // 返回SUCCESS来中断后续事件，自己处理骨粉消耗
                    return@UseBlockCallback net.minecraft.util.ActionResult.SUCCESS
                }
                
                // 检查是否是白桦树叶
                if (blockId.path == "birch_leaves") {
                    // 消耗骨粉（仅服务器端）
                    if (!world.isClient && !player.abilities.creativeMode) {
                        heldStack.decrement(1)
                    }
                    
                    // 播放粒子效果
                    if (world.isClient) {
                        val pos = hitResult.blockPos
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
                    
                    // 掉落1-2个柠檬（仅服务器端）
                    if (!world.isClient) {
                        val lemonCount = Random.nextInt(1, 3)
                        val lemonStack = ItemStack(ModItems.LEMON, lemonCount)
                        net.minecraft.entity.ItemEntity(world, hitResult.blockPos.x + 0.5, hitResult.blockPos.y + 0.5, hitResult.blockPos.z + 0.5, lemonStack).apply {
                            setToDefaultPickupDelay()
                            world.spawnEntity(this)
                        }
                        
                        // 触发柠檬树进度
                        com.hydroceder.hgbg.advancement.ObtainLemonTrigger.getInstance().trigger(player as net.minecraft.server.network.ServerPlayerEntity)
                    }
                    
                    // 返回SUCCESS来中断后续事件，自己处理骨粉消耗
                    return@UseBlockCallback net.minecraft.util.ActionResult.SUCCESS
                }
                
                // 检查是否是垂泪藤植株
                if (blockId.path == "weeping_vines_plant") {
                    // 播放粒子效果
                    if (world.isClient) {
                        val pos = hitResult.blockPos
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
                    
                    // 掉落2-3个绯红菌（仅服务器端）
                    if (!world.isClient) {
                        val crimsonFungusCount = Random.nextInt(2, 4)
                        val crimsonFungusStack = ItemStack(Items.CRIMSON_FUNGUS, crimsonFungusCount)
                        net.minecraft.entity.ItemEntity(world, hitResult.blockPos.x + 0.5, hitResult.blockPos.y + 0.5, hitResult.blockPos.z + 0.5, crimsonFungusStack).apply {
                            setToDefaultPickupDelay()
                            world.spawnEntity(this)
                        }
                        
                        // 触发腐化加剧进度
                        com.hydroceder.hgbg.advancement.BonemealCrimsonTrigger.getInstance().trigger(player as net.minecraft.server.network.ServerPlayerEntity)
                        
                        // 手动消耗骨粉（因为原版没有垂泪藤/绯红菌的催熟逻辑）
                        if (!player.abilities.creativeMode) {
                            heldStack.decrement(1)
                        }
                    }
                    
                    // 返回SUCCESS以确保挥臂动画和消耗
                    return@UseBlockCallback net.minecraft.util.ActionResult.SUCCESS
                }
            }
            return@UseBlockCallback net.minecraft.util.ActionResult.PASS
        })

    }
    
    /**
     * 检查玩家装备是否有滋养附魔
     */
    private fun hasNourishmentEnchantment(player: PlayerEntity): Boolean {
        // 遍历玩家的所有装备槽
        for (slot in EquipmentSlot.values()) {
            val stack = player.getEquippedStack(slot)
            if (hasNourishmentEnchantment(stack)) {
                return true
            }
        }
        return false
    }
    
    /**
     * 检查物品栈是否有滋养附魔
     */
    private fun hasNourishmentEnchantment(stack: ItemStack): Boolean {
        return EnchantmentHelper.getLevel(NourishmentEnchantment.INSTANCE, stack) > 0
    }
    
    /**
     * 检查玩家装备是否有II级滋养附魔
     */
    private fun hasNourishmentEnchantmentLevel2(player: PlayerEntity): Boolean {
        // 遍历玩家的所有装备槽
        for (slot in EquipmentSlot.values()) {
            val stack = player.getEquippedStack(slot)
            if (EnchantmentHelper.getLevel(NourishmentEnchantment.INSTANCE, stack) >= 2) {
                return true
            }
        }
        return false
    }
    
    /**
     * 获取玩家鞋的饱食疾行附魔等级
     */
    private fun getSpeedEnchantmentLevel(player: PlayerEntity): Int {
        val boots = player.getEquippedStack(EquipmentSlot.FEET)
        return EnchantmentHelper.getLevel(SpeedEnchantment.INSTANCE, boots)
    }
    
    /**
     * 注册配方同步事件监听器
     */
    private fun registerRecipeSyncListener() {
        // 监听服务器数据包同步事件
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register { player, joined ->
            // 当配方数据同步时，重新构建烹饪配方缓存
            CookingRecipeCache.buildCache(player.server)
            // 加载锅烹饪JSON配方
            PanCookingRecipeManager.loadRecipesFromServer(player.server)
            logger.info("Cooking recipe cache rebuilt! Cache size: {}", CookingRecipeCache.getCacheSize())
            logger.info("Pan cooking recipes loaded: {}", PanCookingRecipeManager.getAllRecipes().size)
        }
        
        // 监听服务器启动事件，初始化缓存
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            CookingRecipeCache.buildCache(server)
            // 加载锅烹饪JSON配方
            PanCookingRecipeManager.loadRecipesFromServer(server)
            logger.info("Cooking recipe cache initialized! Cache size: {}", CookingRecipeCache.getCacheSize())
            logger.info("Pan cooking recipes loaded: {}", PanCookingRecipeManager.getAllRecipes().size)
        }
    }
    
    private fun registerLootTableModifications() {
        LootTableEvents.MODIFY.register { resourceManager, manager, id, tableBuilder, source ->
            // 检查是否是钓鱼战利品表
            if (id == LootTables.FISHING_GAMEPLAY) {
                // 向所有现有的战利品池中添加物品
                // 生虾添加到鱼类池（权重30）
                tableBuilder.modifyPools { pool ->
                    // 为每个池都添加生虾，让它们的权重自然起作用
                    pool.with(ItemEntry.builder(ModItems.RAW_SHRIMP)
                        .weight(50)
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0f))))
                }
                
                logger.info("Added raw shrimp to fishing loot table!")
            }
        }
    }
    
    /**
     * 获取玩家田野收获者附魔的最高等级
     */
    private fun getFieldHarvesterEnchantmentLevel(player: PlayerEntity): Int {
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
    
    /**
     * 检查玩家是否在耕地附近（3x3范围）
     */
    private fun isNearFarmland(world: World, pos: BlockPos): Boolean {
        for (x in -1..1) {
            for (y in -1..1) {
                for (z in -1..1) {
                    val checkPos = pos.add(x, y, z)
                    val state = world.getBlockState(checkPos)
                    if (state.isOf(net.minecraft.block.Blocks.FARMLAND)) {
                        return true
                    }
                }
            }
        }
        return false
    }
    
    /**
     * 检查方块是否是作物
     */
    private fun isCropBlock(state: net.minecraft.block.BlockState): Boolean {
        val block = state.block
        return block is net.minecraft.block.CropBlock || 
               block is net.minecraft.block.NetherWartBlock ||
               block is net.minecraft.block.CocoaBlock ||
               block is net.minecraft.block.SugarCaneBlock ||
               block is net.minecraft.block.CactusBlock ||
               block is net.minecraft.block.BambooBlock ||
               block is net.minecraft.block.BambooSaplingBlock ||
               block is net.minecraft.block.SweetBerryBushBlock ||
               block is net.minecraft.block.CaveVinesBodyBlock ||
               block is net.minecraft.block.CaveVinesHeadBlock ||
               block is net.minecraft.block.PitcherCropBlock
    }
    
    /**
     * 掉落额外的物品（翻倍）
     */
    private fun dropExtraItems(world: World, pos: BlockPos, state: net.minecraft.block.BlockState) {
        // 获取原始掉落物
        val drops = net.minecraft.block.Block.getDroppedStacks(state, world.server!!.overworld, pos, null)
        for (drop in drops) {
            if (!drop.isEmpty) {
                val extraDrop = drop.copy()
                net.minecraft.block.Block.dropStack(world, pos, extraDrop)
            }
        }
    }
    
    /**
     * 掉落单个物品
     */
    private fun dropItem(world: World, pos: BlockPos, stack: net.minecraft.item.ItemStack) {
        net.minecraft.block.Block.dropStack(world, pos, stack)
    }
    
    /**
     * 注册调味料
     */
    private fun registerSeasonings() {
        // 海盐 - 饱和效果 (1分钟 = 1200 ticks)
        // 可盛放、非瓶装调味料
        SeasoningRegistry.register(SimpleSeasoning(
            "hunger-begone:salt",
            "item.hunger-begone.seasoned.salt",
            ModItems.SALT,
            { user ->
                user.addStatusEffect(net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SATURATION,
                    1200,
                    0,
                    false,
                    true
                ))
            },
            true,
            false
        ))
        
        // 酱油 - 急迫效果 (25秒 = 500 ticks)
        // 可盛放、瓶装调味料
        SeasoningRegistry.register(SimpleSeasoning(
            "hunger-begone:soy_sauce",
            "item.hunger-begone.seasoned.soy_sauce",
            ModItems.SOY_SAUCE,
            { user ->
                user.addStatusEffect(net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.HASTE,
                    500,
                    0,
                    false,
                    true
                ))
            },
            true,
            true
        ))
        
        // 辣椒酱 - 着火 (3秒 = 60 ticks) + 跳跃提升II (35秒 = 700 ticks)
        // 可盛放、瓶装调味料
        SeasoningRegistry.register(SimpleSeasoning(
            "hunger-begone:chili_sauce",
            "item.hunger-begone.seasoned.chili_sauce",
            ModItems.CHILI_BOTTLE,
            { user ->
                user.fireTicks = 60
                user.addStatusEffect(net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.JUMP_BOOST,
                    700,
                    1,
                    false,
                    true
                ))
            },
            true,
            true
        ))
        
        // 肉桂粉 - 速度提升 (15秒 = 600 ticks)
        // 可盛放、非瓶装调味料
        SeasoningRegistry.register(SimpleSeasoning(
            "hunger-begone:cinnamon",
            "item.hunger-begone.seasoned.cinnamon",
            ModItems.CINNAMON,
            { user ->
                user.addStatusEffect(net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SPEED,
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