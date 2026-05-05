package com.hydroceder.hgbg.block;

import com.hydroceder.hgbg.block.entity.OvenBlockEntity;
import com.hydroceder.hgbg.block.entity.StoveBlockEntity;
import com.hydroceder.hgbg.block.entity.MortarAndPestleBlockEntity;
import com.hydroceder.hgbg.block.entity.ShelfBlockEntity;
import com.hydroceder.hgbg.block.entity.CupBlockEntity;
import com.hydroceder.hgbg.block.entity.WoodenCupBlockEntity;
import com.hydroceder.hgbg.block.entity.MetronomeBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模组方块实体类型注册类
 */
public class ModBlockEntityTypes {
    public static final Logger LOGGER = LoggerFactory.getLogger("hunger-begone");
    
    public static BlockEntityType<StoveBlockEntity> STOVE_BLOCK_ENTITY;
    public static final Identifier STOVE_BLOCK_ENTITY_ID = new Identifier("hunger-begone", "stove");
    
    public static BlockEntityType<OvenBlockEntity> OVEN_BLOCK_ENTITY;
    public static final Identifier OVEN_BLOCK_ENTITY_ID = new Identifier("hunger-begone", "oven");
    
    public static BlockEntityType<MortarAndPestleBlockEntity> MORTAR_AND_PESTLE_BLOCK_ENTITY;
    public static final Identifier MORTAR_AND_PESTLE_BLOCK_ENTITY_ID = new Identifier("hunger-begone", "mortar_and_pestle");
    
    public static BlockEntityType<ShelfBlockEntity> SHELF_BLOCK_ENTITY;
    public static final Identifier SHELF_BLOCK_ENTITY_ID = new Identifier("hunger-begone", "shelf");
    
    public static BlockEntityType<CupBlockEntity> CUP_BLOCK_ENTITY;
    public static final Identifier CUP_BLOCK_ENTITY_ID = new Identifier("hunger-begone", "cup");

    public static BlockEntityType<WoodenCupBlockEntity> WOODEN_CUP_BLOCK_ENTITY;
    public static final Identifier WOODEN_CUP_BLOCK_ENTITY_ID = new Identifier("hunger-begone", "wooden_cup");

    public static BlockEntityType<MetronomeBlockEntity> METRONOME_BLOCK_ENTITY;
    public static final Identifier METRONOME_BLOCK_ENTITY_ID = new Identifier("hunger-begone", "beat");

    public static BlockEntityType<com.hydroceder.hgbg.block.entity.CoinOperatedMachineBlockEntity> COIN_OPERATED_MACHINE_BLOCK_ENTITY;
    public static final Identifier COIN_OPERATED_MACHINE_BLOCK_ENTITY_ID = new Identifier("hunger-begone", "coin_operated_machine");
    
    /**
     * 注册所有方块实体类型
     */
    public static void register() {
        STOVE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            STOVE_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(StoveBlockEntity::new, ModBlocks.STOVE).build()
        );
        
        OVEN_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            OVEN_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(OvenBlockEntity::new, ModBlocks.OVEN).build()
        );
        
        MORTAR_AND_PESTLE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            MORTAR_AND_PESTLE_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(MortarAndPestleBlockEntity::new, ModBlocks.MORTAR_AND_PESTLE).build()
        );
        
        SHELF_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            SHELF_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(ShelfBlockEntity::new, ModBlocks.SHELF).build()
        );
        
        CUP_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            CUP_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(CupBlockEntity::new, ModBlocks.EMPTY_CUP).build()
        );

        WOODEN_CUP_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            WOODEN_CUP_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(WoodenCupBlockEntity::new, ModBlocks.WOODEN_CUP).build()
        );

        METRONOME_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            METRONOME_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(MetronomeBlockEntity::new, ModBlocks.METRONOME).build()
        );

        COIN_OPERATED_MACHINE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            COIN_OPERATED_MACHINE_BLOCK_ENTITY_ID,
            FabricBlockEntityTypeBuilder.create(com.hydroceder.hgbg.block.entity.CoinOperatedMachineBlockEntity::new, ModBlocks.COIN_OPERATED_MACHINE).build()
        );
        
        LOGGER.info("Block entity types registered successfully!");
    }
}