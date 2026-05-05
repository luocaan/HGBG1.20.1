package com.hydroceder.hgbg.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModEntities {
    private static final Logger LOGGER = LoggerFactory.getLogger("hunger-begone");
    private static final String MOD_ID = "hunger-begone";

    public static final EntityType<SofaEntity> SOFA = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID, "sofa"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, SofaEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 1.0f))
                    .build()
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(SOFA, createSofaAttributes());
        LOGGER.info("HGBG entities registered!");
    }

    private static DefaultAttributeContainer.Builder createSofaAttributes() {
        return SofaEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5);
    }
}
