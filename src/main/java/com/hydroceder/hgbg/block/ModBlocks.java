package com.hydroceder.hgbg.block;

import com.hydroceder.hgbg.item.ModItems;
import com.hydroceder.hgbg.item.food.PlacableBlockItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WoodType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hydroceder.hgbg.structure.ModFeatures;

/**
 * 模组方块注册类
 */
public class ModBlocks {
    public static final Logger LOGGER = LoggerFactory.getLogger("hunger-begone");
    
    private static final java.util.List<CropBlock> HGBG_CROPS = new java.util.ArrayList<>();
    
    public static void registerCropBlock(CropBlock crop) {
        HGBG_CROPS.add(crop);
    }
    
    public static java.util.List<CropBlock> getHgbgCrops() {
        return java.util.Collections.unmodifiableList(HGBG_CROPS);
    }
    
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

    public static Block MELON_FRUIT_BOWL;
    public static final Identifier MELON_FRUIT_BOWL_ID = new Identifier("hunger-begone", "melon_fruit_bowl");

    public static Block CARROT_BOWL;
    public static final Identifier CARROT_BOWL_ID = new Identifier("hunger-begone", "carrot_bowl");

    public static Block CREAMY_MUSHROOM_SOUP_BLOCK;
    public static final Identifier CREAMY_MUSHROOM_SOUP_BLOCK_ID = new Identifier("hunger-begone", "creamy_mushroom_soup_block");

    public static Block LEMON_PICKLE_BLOCK;
    public static final Identifier LEMON_PICKLE_BLOCK_ID = new Identifier("hunger-begone", "lemon_pickle_block");

    public static Block STARE_AT_CUBE_PIE;
    public static final Identifier STARE_AT_CUBE_PIE_ID = new Identifier("hunger-begone", "stare_at_cube_pie");

    public static Block STINKY_TOFU;
    public static final Identifier STINKY_TOFU_ID = new Identifier("hunger-begone", "stinky_tofu");

    public static Block WUGUANG_CAKE;
    public static final Identifier WUGUANG_CAKE_ID = new Identifier("hunger-begone", "wuguang_cake");

    public static Block WELLINGTON;
    public static final Identifier WELLINGTON_ID = new Identifier("hunger-begone", "wellington");

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

    // 棕榈原木
    public static Block COCONUT_LOG;
    public static final Identifier COCONUT_LOG_ID = new Identifier("hunger-begone", "coconut_log");

    // 棕榈树叶
    public static Block COCONUT_LEAVES;
    public static final Identifier COCONUT_LEAVES_ID = new Identifier("hunger-begone", "coconut_leaves");

    // 棕榈树苗
    public static Block COCONUT_SAPLING;
    public static final Identifier COCONUT_SAPLING_ID = new Identifier("hunger-begone", "coconut_sapling");

    public static Block EGGPLANT_CROP;
    public static final Identifier EGGPLANT_CROP_ID = new Identifier("hunger-begone", "eggplant_crop");
    public static final Identifier EGGPLANT_SEED_ID = new Identifier("hunger-begone", "eggplant_seed");

    public static Block SOYBEAN_CROP;
    public static final Identifier SOYBEAN_CROP_ID = new Identifier("hunger-begone", "soybean_crop");
    public static final Identifier SOYBEAN_ID = new Identifier("hunger-begone", "soybean");

    // 棕榈木板
    public static Block PALM_PLANKS;
    public static final Identifier PALM_PLANKS_ID = new Identifier("hunger-begone", "palm_planks");

    // 棕榈台阶
    public static Block PALM_SLAB;
    public static final Identifier PALM_SLAB_ID = new Identifier("hunger-begone", "palm_slab");

    // 棕榈楼梯
    public static Block PALM_STAIRS;
    public static final Identifier PALM_STAIRS_ID = new Identifier("hunger-begone", "palm_stairs");

    // 棕榈栅栏
    public static Block PALM_FENCE;
    public static final Identifier PALM_FENCE_ID = new Identifier("hunger-begone", "palm_fence");

    // 棕榈栅栏门
    public static Block PALM_FENCE_GATE;
    public static final Identifier PALM_FENCE_GATE_ID = new Identifier("hunger-begone", "palm_fence_gate");

    // 棕榈压力板
    public static Block PALM_PRESSURE_PLATE;
    public static final Identifier PALM_PRESSURE_PLATE_ID = new Identifier("hunger-begone", "palm_pressure_plate");

    // 棕榈按钮
    public static Block PALM_BUTTON;
    public static final Identifier PALM_BUTTON_ID = new Identifier("hunger-begone", "palm_button");

    // 悬挂椰子
    public static Block HANGING_COCONUT;
    public static final Identifier HANGING_COCONUT_ID = new Identifier("hunger-begone", "hanging_coconut");

    // 炖锅实例
    public static Block STEW_POT;
    public static final Identifier STEW_POT_ID = new Identifier("hunger-begone", "stew_pot");
    
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

        ModItems.ORLEANS_ROASTED_CHICKEN = new com.hydroceder.hgbg.item.food.OrleansRoastedChickenItem(
            ROASTED_CHICKEN,
            new FabricItemSettings().maxCount(1)
        );
        Registry.register(Registries.ITEM, new Identifier("hunger-begone", "orleans_roasted_chicken"), ModItems.ORLEANS_ROASTED_CHICKEN);
        
        // 注册紫颂果盘方块
        CHORUS_FRUIT_BOWL = new ChorusFruitBowlBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, CHORUS_FRUIT_BOWL_ID, CHORUS_FRUIT_BOWL);
        
        PlacableBlockItem cfbItem = new PlacableBlockItem(CHORUS_FRUIT_BOWL, new FabricItemSettings());
        Registry.register(Registries.ITEM, CHORUS_FRUIT_BOWL_ID, cfbItem);
        
        // 注册苹果果盘方块
        APPLE_FRUIT_BOWL = new AppleFruitBowlBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, APPLE_FRUIT_BOWL_ID, APPLE_FRUIT_BOWL);
        
        PlacableBlockItem afbItem = new PlacableBlockItem(APPLE_FRUIT_BOWL, new FabricItemSettings());
        Registry.register(Registries.ITEM, APPLE_FRUIT_BOWL_ID, afbItem);

        // 注册西瓜果盘方块
        MELON_FRUIT_BOWL = new MelonFruitBowlBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, MELON_FRUIT_BOWL_ID, MELON_FRUIT_BOWL);

        PlacableBlockItem melonFruitBowlItem = new PlacableBlockItem(MELON_FRUIT_BOWL, new FabricItemSettings());
        Registry.register(Registries.ITEM, MELON_FRUIT_BOWL_ID, melonFruitBowlItem);

        // 注册胡萝卜摆盘方块
        CARROT_BOWL = new CarrotBowlBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, CARROT_BOWL_ID, CARROT_BOWL);

        PlacableBlockItem carrotBowlItem = new PlacableBlockItem(CARROT_BOWL, new FabricItemSettings());
        Registry.register(Registries.ITEM, CARROT_BOWL_ID, carrotBowlItem);

        // 注册奶油蘑菇汤方块（使用BlockItem以支持中键选取）
        CREAMY_MUSHROOM_SOUP_BLOCK = new CreamyMushroomSoupBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, CREAMY_MUSHROOM_SOUP_BLOCK_ID, CREAMY_MUSHROOM_SOUP_BLOCK);

        ModItems.CREAMY_MUSHROOM_SOUP = new com.hydroceder.hgbg.item.food.CreamyMushroomSoupItem(
            CREAMY_MUSHROOM_SOUP_BLOCK,
            new FabricItemSettings().maxCount(1)
        );
        Registry.register(Registries.ITEM, new Identifier("hunger-begone", "creamy_mushroom_soup"), ModItems.CREAMY_MUSHROOM_SOUP);

        // 注册柠檬泡菜方块
        LEMON_PICKLE_BLOCK = new LemonPickleBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, LEMON_PICKLE_BLOCK_ID, LEMON_PICKLE_BLOCK);

        ModItems.LEMON_PICKLE = new com.hydroceder.hgbg.item.food.LemonPickleItem(
            LEMON_PICKLE_BLOCK,
            new FabricItemSettings().maxCount(1)
        );
        Registry.register(Registries.ITEM, new Identifier("hunger-begone", "lemon_pickle"), ModItems.LEMON_PICKLE);

        // 注册仰望夜空派方块
        STARE_AT_CUBE_PIE = new StareAtCubePieBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, STARE_AT_CUBE_PIE_ID, STARE_AT_CUBE_PIE);

        ModItems.STARE_AT_CUBE_PIE = new com.hydroceder.hgbg.item.food.StareAtCubePieItem(
            STARE_AT_CUBE_PIE,
            new FabricItemSettings().maxCount(1)
        );
        Registry.register(Registries.ITEM, new Identifier("hunger-begone", "stare_at_cube_pie"), ModItems.STARE_AT_CUBE_PIE);

        // 注册臭豆腐方块
        STINKY_TOFU = new StinkyTofuBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, STINKY_TOFU_ID, STINKY_TOFU);

        ModItems.STINKY_TOFU = new com.hydroceder.hgbg.item.food.StinkyTofuItem(
            STINKY_TOFU,
            new FabricItemSettings().maxCount(1)
        );
        Registry.register(Registries.ITEM, new Identifier("hunger-begone", "stinky_tofu"), ModItems.STINKY_TOFU);

        // 注册五光糕方块
        WUGUANG_CAKE = new WuguangCakeBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, WUGUANG_CAKE_ID, WUGUANG_CAKE);

        ModItems.WUGUANG_CAKE = new com.hydroceder.hgbg.item.food.WuguangCakeItem(
            WUGUANG_CAKE,
            new FabricItemSettings().maxCount(1)
        );
        Registry.register(Registries.ITEM, new Identifier("hunger-begone", "wuguang_cake"), ModItems.WUGUANG_CAKE);

        // 注册惠灵顿牛排方块
        WELLINGTON = new WellingtonBlock(FabricBlockSettings.create()
                .strength(0.1f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOL)
                .nonOpaque());
        Registry.register(Registries.BLOCK, WELLINGTON_ID, WELLINGTON);

        ModItems.WELLINGTON = new com.hydroceder.hgbg.item.food.WellingtonItem(
            WELLINGTON,
            new FabricItemSettings().maxCount(1)
        );
        Registry.register(Registries.ITEM, new Identifier("hunger-begone", "wellington"), ModItems.WELLINGTON);
        
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

        PlacableBlockItem breadPlateItem = new PlacableBlockItem(BREAD_PLATE, new FabricItemSettings());
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

        // 注册棕榈原木
        COCONUT_LOG = new PillarBlock(FabricBlockSettings.create()
                .strength(2.0f)
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOD));
        Registry.register(Registries.BLOCK, COCONUT_LOG_ID, COCONUT_LOG);

        BlockItem coconutLogItem = new BlockItem(COCONUT_LOG, new FabricItemSettings());
        Registry.register(Registries.ITEM, COCONUT_LOG_ID, coconutLogItem);

        // 注册棕榈树叶
        COCONUT_LEAVES = new LeavesBlock(FabricBlockSettings.create()
                .strength(0.2f)
                .nonOpaque()
                .sounds(net.minecraft.sound.BlockSoundGroup.GRASS)
                .ticksRandomly());
        Registry.register(Registries.BLOCK, COCONUT_LEAVES_ID, COCONUT_LEAVES);

        BlockItem coconutLeavesItem = new BlockItem(COCONUT_LEAVES, new FabricItemSettings());
        Registry.register(Registries.ITEM, COCONUT_LEAVES_ID, coconutLeavesItem);

        // 注册棕榈树苗
        COCONUT_SAPLING = new CoconutSaplingBlock(new SaplingGenerator() {
            @Override
            protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(net.minecraft.util.math.random.Random random, boolean bees) {
                return ModFeatures.PALM_TREE_CONFIGURED_KEY;
            }
        }, FabricBlockSettings.create()
                .strength(0.0f)
                .nonOpaque()
                .sounds(net.minecraft.sound.BlockSoundGroup.GRASS)
                .noCollision()
                .ticksRandomly());
        Registry.register(Registries.BLOCK, COCONUT_SAPLING_ID, COCONUT_SAPLING);

        BlockItem coconutSaplingItem = new BlockItem(COCONUT_SAPLING, new FabricItemSettings());
        Registry.register(Registries.ITEM, COCONUT_SAPLING_ID, coconutSaplingItem);

        // 注册棕榈木板
        PALM_PLANKS = new Block(FabricBlockSettings.create()
                .strength(2.0f, 3.0f)
                .sounds(BlockSoundGroup.WOOD));
        Registry.register(Registries.BLOCK, PALM_PLANKS_ID, PALM_PLANKS);
        Registry.register(Registries.ITEM, PALM_PLANKS_ID, new BlockItem(PALM_PLANKS, new FabricItemSettings()));

        // 注册棕榈台阶
        PALM_SLAB = new SlabBlock(FabricBlockSettings.create()
                .strength(2.0f, 3.0f)
                .sounds(BlockSoundGroup.WOOD));
        Registry.register(Registries.BLOCK, PALM_SLAB_ID, PALM_SLAB);
        Registry.register(Registries.ITEM, PALM_SLAB_ID, new BlockItem(PALM_SLAB, new FabricItemSettings()));

        // 注册棕榈楼梯
        PALM_STAIRS = new StairsBlock(PALM_PLANKS.getDefaultState(), FabricBlockSettings.create()
                .strength(2.0f, 3.0f)
                .sounds(BlockSoundGroup.WOOD));
        Registry.register(Registries.BLOCK, PALM_STAIRS_ID, PALM_STAIRS);
        Registry.register(Registries.ITEM, PALM_STAIRS_ID, new BlockItem(PALM_STAIRS, new FabricItemSettings()));

        // 注册棕榈栅栏
        PALM_FENCE = new net.minecraft.block.FenceBlock(FabricBlockSettings.create()
                .strength(2.0f, 3.0f)
                .sounds(BlockSoundGroup.WOOD));
        Registry.register(Registries.BLOCK, PALM_FENCE_ID, PALM_FENCE);
        Registry.register(Registries.ITEM, PALM_FENCE_ID, new BlockItem(PALM_FENCE, new FabricItemSettings()));

        // 注册棕榈栅栏门
        PALM_FENCE_GATE = new net.minecraft.block.FenceGateBlock(
                FabricBlockSettings.create().strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD),
                WoodType.OAK);
        Registry.register(Registries.BLOCK, PALM_FENCE_GATE_ID, PALM_FENCE_GATE);
        Registry.register(Registries.ITEM, PALM_FENCE_GATE_ID, new BlockItem(PALM_FENCE_GATE, new FabricItemSettings()));

        // 注册棕榈压力板
        PALM_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                FabricBlockSettings.create().strength(0.5f).sounds(BlockSoundGroup.WOOD).noCollision(),
                BlockSetType.OAK);
        Registry.register(Registries.BLOCK, PALM_PRESSURE_PLATE_ID, PALM_PRESSURE_PLATE);
        Registry.register(Registries.ITEM, PALM_PRESSURE_PLATE_ID, new BlockItem(PALM_PRESSURE_PLATE, new FabricItemSettings()));

        // 注册棕榈按钮
        PALM_BUTTON = new ButtonBlock(
                FabricBlockSettings.create().strength(0.5f).sounds(BlockSoundGroup.WOOD).noCollision(),
                BlockSetType.OAK, 30, true);
        Registry.register(Registries.BLOCK, PALM_BUTTON_ID, PALM_BUTTON);
        Registry.register(Registries.ITEM, PALM_BUTTON_ID, new BlockItem(PALM_BUTTON, new FabricItemSettings()));

        // 注册悬挂椰子
        HANGING_COCONUT = new HangingCoconutBlock(FabricBlockSettings.create()
                .strength(0.5f)
                .requiresTool()
                .sounds(net.minecraft.sound.BlockSoundGroup.WOOD)
                .nonOpaque()
                .noCollision());
        Registry.register(Registries.BLOCK, HANGING_COCONUT_ID, HANGING_COCONUT);
        Registry.register(Registries.ITEM, HANGING_COCONUT_ID, new BlockItem(HANGING_COCONUT, new FabricItemSettings()));

        EGGPLANT_CROP = new EggplantCropBlock(null);
        Registry.register(Registries.BLOCK, EGGPLANT_CROP_ID, EGGPLANT_CROP);
        registerCropBlock((CropBlock) EGGPLANT_CROP);

        ModItems.EGGPLANT_SEED = new BlockItem(EGGPLANT_CROP, new FabricItemSettings()) {
            @Override
            public String getTranslationKey() {
                return "item.hunger-begone.eggplant_seed";
            }
        };
        Registry.register(Registries.ITEM, EGGPLANT_SEED_ID, ModItems.EGGPLANT_SEED);

        ((EggplantCropBlock) EGGPLANT_CROP).setSeedsItem(ModItems.EGGPLANT_SEED);

        SOYBEAN_CROP = new SoybeanCropBlock(null);
        Registry.register(Registries.BLOCK, SOYBEAN_CROP_ID, SOYBEAN_CROP);
        registerCropBlock((CropBlock) SOYBEAN_CROP);

        ModItems.SOYBEAN = new BlockItem(SOYBEAN_CROP, new FabricItemSettings().food(com.hydroceder.hgbg.item.ModFoodComponents.SOYBEAN));
        Registry.register(Registries.ITEM, SOYBEAN_ID, ModItems.SOYBEAN);

        ((SoybeanCropBlock) SOYBEAN_CROP).setSeedsItem(ModItems.SOYBEAN);

        STEW_POT = new StewPotBlock(FabricBlockSettings.create()
                .strength(1.0f)
                .nonOpaque()
                .sounds(net.minecraft.sound.BlockSoundGroup.METAL));
        Registry.register(Registries.BLOCK, STEW_POT_ID, STEW_POT);
        Registry.register(Registries.ITEM, STEW_POT_ID, new BlockItem(STEW_POT, new FabricItemSettings()));

        LOGGER.info("Blocks registered successfully!");
    }
}