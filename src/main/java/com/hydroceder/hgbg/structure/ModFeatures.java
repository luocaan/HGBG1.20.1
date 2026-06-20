package com.hydroceder.hgbg.structure;

import com.hydroceder.hgbg.HgbgMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.BiomeKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModFeatures {
    private static final Logger LOGGER = LoggerFactory.getLogger(HgbgMod.MOD_ID);
    
    public static final Feature<DefaultFeatureConfig> SMALL_CAMP = new SmallCampFeature();
    
    public static final Feature<DefaultFeatureConfig> SMALL_GARDEN = new SmallGardenFeature();
    
    @SuppressWarnings("unchecked")
    public static final RegistryKey SMALL_CAMP_PLACED_KEY =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(HgbgMod.MOD_ID, "small_camp"));
    
    @SuppressWarnings("unchecked")
    public static final RegistryKey SMALL_GARDEN_PLACED_KEY =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(HgbgMod.MOD_ID, "small_garden"));
    
    // 棕榈树世界生成特征键
    public static final Feature<DefaultFeatureConfig> PALM_TREE = new PalmTreeFeature();
    
    @SuppressWarnings("unchecked")
    public static final RegistryKey PALM_TREE_CONFIGURED_KEY =
        RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(HgbgMod.MOD_ID, "palm_tree"));
    
    @SuppressWarnings("unchecked")
    public static final RegistryKey PALM_TREE_PLACED_KEY =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(HgbgMod.MOD_ID, "palm_tree"));
    
    public static void register() {
        Registry.register(Registries.FEATURE, new Identifier(HgbgMod.MOD_ID, "small_camp"), SMALL_CAMP);
        
        Registry.register(Registries.FEATURE, new Identifier(HgbgMod.MOD_ID, "small_garden"), SMALL_GARDEN);

        // 注册棕榈树世界生成特征
        Registry.register(Registries.FEATURE, new Identifier(HgbgMod.MOD_ID, "palm_tree"), PALM_TREE);
        
        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Feature.SURFACE_STRUCTURES,
            SMALL_CAMP_PLACED_KEY
        );
        
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(
                BiomeKeys.PLAINS,
                BiomeKeys.SUNFLOWER_PLAINS,
                BiomeKeys.MEADOW,
                BiomeKeys.FOREST,
                BiomeKeys.TAIGA,
                BiomeKeys.FLOWER_FOREST,
                BiomeKeys.BIRCH_FOREST,
                BiomeKeys.OLD_GROWTH_BIRCH_FOREST
            ),
            GenerationStep.Feature.VEGETAL_DECORATION,
            SMALL_GARDEN_PLACED_KEY
        );

        // 棕榈树生成于沙滩和海岸生物群系
        BiomeModifications.addFeature(
            BiomeSelectors.includeByKey(
                BiomeKeys.BEACH,
                BiomeKeys.SNOWY_BEACH,
                BiomeKeys.STONY_SHORE
            ),
            GenerationStep.Feature.VEGETAL_DECORATION,
            PALM_TREE_PLACED_KEY
        );
        
        LOGGER.info("Mod features registered successfully!");
    }
}
