package com.hydroceder.hgbg

import com.hydroceder.hgbg.block.ModBlocks
import com.hydroceder.hgbg.item.ModItems
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

/**
 * 客户端初始化类
 * 处理客户端特定的渲染设置
 */
object HungerBegoneClient : ClientModInitializer {
    override fun onInitializeClient() {
        // 设置烤箱方块使用透明渲染层（支持透明纹理）
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OVEN, RenderLayer.getCutout())
        // 设置置物架方块使用透明渲染层（支持透明纹理）
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SHELF, RenderLayer.getCutout())
        
        // 注册发光果酱瓶的动态纹理谓词
        ModelPredicateProviderRegistry.register(
            ModItems.SHINY_BERRY_JAM_BOTTLE,
            Identifier("hunger-begone", "animation_frame")
        ) { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            // 使用游戏时间计算动画帧，每秒切换一次
            // 20 ticks = 1秒，所以每20 ticks切换一次
            val time = (world?.time ?: entity?.age ?: 0).toLong()
            // 每帧持续20 ticks（1秒），两帧动画：0.0f 和 0.5f
            if ((time / 20L) % 2L == 0L) 0.0f else 0.5f
        }
    }
}
