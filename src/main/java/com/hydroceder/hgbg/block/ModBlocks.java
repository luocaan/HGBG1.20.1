package com.hydroceder.hgbg.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模组方块注册类
 */
public class ModBlocks {
    public static final Logger LOGGER = LoggerFactory.getLogger("hunger-begone");
    
    public static Block STOVE;
    public static final Identifier STOVE_ID = new Identifier("hunger-begone", "stove");
    
    public static Block OVEN;
    public static final Identifier OVEN_ID = new Identifier("hunger-begone", "oven");
    
    public static Block MORTAR_AND_PESTLE;
    public static final Identifier MORTAR_AND_PESTLE_ID = new Identifier("hunger-begone", "mortar_and_pestle");
    
    public static Block SHELF;
    public static final Identifier SHELF_ID = new Identifier("hunger-begone", "shelf");
    
    /**
     * 注册所有方块
     */
    public static void register() {
        // 注册灶台
        STOVE = new StoveBlock(FabricBlockSettings.create()
                .strength(0.3f)
                .requiresTool()
                .nonOpaque()
                .luminance(state -> state.get(StoveBlock.HAS_PAN) ? 14 : 0));
        Registry.register(Registries.BLOCK, STOVE_ID, STOVE);
        
        BlockItem stoveItem = new BlockItem(STOVE, new FabricItemSettings());
        Registry.register(Registries.ITEM, STOVE_ID, stoveItem);
        
        // 注册烤箱
        OVEN = new OvenBlock(FabricBlockSettings.create()
                .strength(1.5f, 6.0f)  // 硬度1.5（与石头相同），抗爆性6.0
                .requiresTool()
                .nonOpaque());
        Registry.register(Registries.BLOCK, OVEN_ID, OVEN);
        
        BlockItem ovenItem = new BlockItem(OVEN, new FabricItemSettings());
        Registry.register(Registries.ITEM, OVEN_ID, ovenItem);
        
        // 注册研钵和杵
        MORTAR_AND_PESTLE = new MortarAndPestleBlock(FabricBlockSettings.create()
                .strength(1.2f, 4.0f)
                .requiresTool()
                .nonOpaque());
        Registry.register(Registries.BLOCK, MORTAR_AND_PESTLE_ID, MORTAR_AND_PESTLE);
        
        BlockItem mortarItem = new BlockItem(MORTAR_AND_PESTLE, new FabricItemSettings());
        Registry.register(Registries.ITEM, MORTAR_AND_PESTLE_ID, mortarItem);
        
        // 注册置物架
        SHELF = new ShelfBlock(FabricBlockSettings.create()
                .strength(1.0f, 3.0f)
                .requiresTool()
                .nonOpaque());
        Registry.register(Registries.BLOCK, SHELF_ID, SHELF);
        
        BlockItem shelfItem = new BlockItem(SHELF, new FabricItemSettings());
        Registry.register(Registries.ITEM, SHELF_ID, shelfItem);
        
        LOGGER.info("Blocks registered successfully!");
    }
}
