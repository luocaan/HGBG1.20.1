package com.hydroceder.hgbg

import com.hydroceder.hgbg.block.ModBlocks
import com.hydroceder.hgbg.block.ModBlockEntityTypes
import com.hydroceder.hgbg.client.model.SofaModel
import com.hydroceder.hgbg.client.model.MetronomePendulumModel
import com.hydroceder.hgbg.client.render.block.MetronomeBlockEntityRenderer
import com.hydroceder.hgbg.client.render.entity.SofaEntityRenderer
import com.hydroceder.hgbg.entity.ModEntities
import com.hydroceder.hgbg.item.ModItems
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
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
        // 设置节拍器方块使用透明渲染层（支持BER渲染）
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.METRONOME, RenderLayer.getCutout())

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SEA_GLOW_LANTERN, RenderLayer.getCutout())

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EGGPLANT_CROP, RenderLayer.getCutout())

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOYBEAN_CROP, RenderLayer.getCutout())

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STEW_POT, RenderLayer.getCutout())

        // 注册沙发实体模型层
        EntityModelLayerRegistry.registerModelLayer(
            SofaModel.LAYER
        ) { SofaModel.getTexturedModelData() }

        // 注册节拍器摆锤模型层
        EntityModelLayerRegistry.registerModelLayer(
            MetronomePendulumModel.LAYER
        ) { MetronomePendulumModel.getTexturedModelData() }
        
        // 注册沙发实体渲染器
        EntityRendererRegistry.register(ModEntities.SOFA, ::SofaEntityRenderer)

        // 注册节拍器方块实体渲染器（使用原生API替代弃用的BlockEntityRendererRegistry）
        BlockEntityRendererFactories.register(ModBlockEntityTypes.METRONOME_BLOCK_ENTITY)
        { ctx: BlockEntityRendererFactory.Context -> MetronomeBlockEntityRenderer(ctx) }
        
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