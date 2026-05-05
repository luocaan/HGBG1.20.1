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
    
    public static Block ROASTED_CHICKEN;
    public static final Identifier ROASTED_CHICKEN_ID = new Identifier("hunger-begone", "roasted_chicken");
    
    public static Block CHORUS_FRUIT_BOWL;
    public static final Identifier CHORUS_FRUIT_BOWL_ID = new Identifier("hunger-begone", "chorus_fruit_bowl");
    
    public static Block APPLE_FRUIT_BOWL;
    public static final Identifier APPLE_FRUIT_BOWL_ID = new Identifier("hunger-begone", "apple_fruit_bowl");
    
    public static Block EMPTY_CUP;
    public static final Identifier EMPTY_CUP_ID = new Identifier("hunger-begone", "empty_cup");

    public static Block WOODEN_CUP;
    public static final Identifier WOODEN_CUP_ID = new Identifier("hunger-begone", "wooden_cup");

    public static Block BREAD_PLATE;
    public static final Identifier BREAD_PLATE_ID = new Identifier("hunger-begone", "bread_plate");

    public static Block METRONOME;
    public static final Identifier METRONOME_ID = new Identifier("hunger-begone", "beat");

    public static Block COIN_OPERATED_MACHINE;
    public static final Identifier COIN_OPERATED_MACHINE_ID = new Identifier("hunger-begone", "coin_operated_machine");

    public static Block SEA_GLOW_LANTERN;
    public static final Identifier SEA_GLOW_LANTERN_ID = new Identifier("hunger-begone", "sea_glow_lantern");
    
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
        
        // 注册烤鸡方块
        ROASTED_CHICKEN = new RoastedChickenBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, ROASTED_CHICKEN_ID, ROASTED_CHICKEN);
        
        // 注意：不创建 BlockItem，因为奥尔良烤鸡物品本身就是放置物
        
        // 注册紫颂果盘方块
        CHORUS_FRUIT_BOWL = new ChorusFruitBowlBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, CHORUS_FRUIT_BOWL_ID, CHORUS_FRUIT_BOWL);
        
        BlockItem cfbItem = new BlockItem(CHORUS_FRUIT_BOWL, new FabricItemSettings());
        Registry.register(Registries.ITEM, CHORUS_FRUIT_BOWL_ID, cfbItem);
        
        // 注册苹果果盘方块
        APPLE_FRUIT_BOWL = new AppleFruitBowlBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, APPLE_FRUIT_BOWL_ID, APPLE_FRUIT_BOWL);
        
        BlockItem afbItem = new BlockItem(APPLE_FRUIT_BOWL, new FabricItemSettings());
        Registry.register(Registries.ITEM, APPLE_FRUIT_BOWL_ID, afbItem);
        
        // 注册空杯子方块
        EMPTY_CUP = new EmptyCupBlock(FabricBlockSettings.create()
                .strength(0.5f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOD)
                .nonOpaque());
        Registry.register(Registries.BLOCK, EMPTY_CUP_ID, EMPTY_CUP);
        
        BlockItem cupItem = new BlockItem(EMPTY_CUP, new FabricItemSettings().maxCount(1));
        Registry.register(Registries.ITEM, EMPTY_CUP_ID, cupItem);

        // 注册木杯方块
        WOODEN_CUP = new WoodenCupBlock(FabricBlockSettings.create()
                .strength(0.5f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOD)
                .nonOpaque());
        Registry.register(Registries.BLOCK, WOODEN_CUP_ID, WOODEN_CUP);

        BlockItem woodenCupItem = new BlockItem(WOODEN_CUP, new FabricItemSettings());
        Registry.register(Registries.ITEM, WOODEN_CUP_ID, woodenCupItem);

        // 注册面包盘方块
        BREAD_PLATE = new BreadPlateBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, BREAD_PLATE_ID, BREAD_PLATE);

        BlockItem breadPlateItem = new BlockItem(BREAD_PLATE, new FabricItemSettings());
        Registry.register(Registries.ITEM, BREAD_PLATE_ID, breadPlateItem);

        METRONOME = new MetronomeBlock(FabricBlockSettings.create()
                .strength(1.0f, 3.0f)
                .nonOpaque());
        Registry.register(Registries.BLOCK, METRONOME_ID, METRONOME);

        BlockItem metronomeItem = new BlockItem(METRONOME, new FabricItemSettings());
        Registry.register(Registries.ITEM, METRONOME_ID, metronomeItem);

        SEA_GLOW_LANTERN = new SeaGlowLanternBlock(FabricBlockSettings.create()
                .strength(2.0f)
                .requiresTool()
                .nonOpaque()
                .luminance(state -> state.get(SeaGlowLanternBlock.LIT) ? 15 : 8)
                .sounds(net.minecraft.sound.BlockSoundGroup.LANTERN));
        Registry.register(Registries.BLOCK, SEA_GLOW_LANTERN_ID, SEA_GLOW_LANTERN);

        BlockItem seaGlowLanternItem = new BlockItem(SEA_GLOW_LANTERN, new FabricItemSettings());
        Registry.register(Registries.ITEM, SEA_GLOW_LANTERN_ID, seaGlowLanternItem);
        
        COIN_OPERATED_MACHINE = new CoinOperatedMachineBlock(FabricBlockSettings.create()
                .strength(2.5f, 6.0f)
                .nonOpaque()
                .sounds(net.minecraft.sound.BlockSoundGroup.METAL));
        Registry.register(Registries.BLOCK, COIN_OPERATED_MACHINE_ID, COIN_OPERATED_MACHINE);

        BlockItem coinOperatedMachineItem = new BlockItem(COIN_OPERATED_MACHINE, new FabricItemSettings());
        Registry.register(Registries.ITEM, COIN_OPERATED_MACHINE_ID, coinOperatedMachineItem);
        
        LOGGER.info("Blocks registered successfully!");
    }
}