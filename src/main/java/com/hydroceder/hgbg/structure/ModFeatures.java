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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModFeatures {
    private static final Logger LOGGER = LoggerFactory.getLogger(HgbgMod.MOD_ID);
    
    public static final Feature<DefaultFeatureConfig> SMALL_CAMP = new SmallCampFeature();
    
    @SuppressWarnings("unchecked")
    public static final RegistryKey SMALL_CAMP_PLACED_KEY =
        RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(HgbgMod.MOD_ID, "small_camp"));
    
    public static void register() {
        Registry.register(Registries.FEATURE, new Identifier(HgbgMod.MOD_ID, "small_camp"), SMALL_CAMP);
        
        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Feature.SURFACE_STRUCTURES,
            SMALL_CAMP_PLACED_KEY
        );
        
        LOGGER.info("Mod features registered successfully!");
    }
}
