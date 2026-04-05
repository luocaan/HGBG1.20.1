package com.hydroceder.hgbg.structure;

import com.hydroceder.hgbg.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SmallCampFeature extends Feature<DefaultFeatureConfig> {
    private static final Identifier LOOT_TABLE = new Identifier("hunger-begone", "chests/camp_loot");
    
    public SmallCampFeature() {
        super(DefaultFeatureConfig.CODEC);
    }
    
    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();
        
        BlockPos surfacePos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, origin).down();
        
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy <= 2; dy++) {
                    BlockPos pos = surfacePos.add(dx, dy, dz);
                    
                    if (dy == 0) {
                        world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
                    } else if (dy == 1) {
                        if (dx == -1 && dz == -1) {
                            world.setBlockState(pos, Blocks.STONE_BRICKS.getDefaultState(), 3);
                        } else if (dx == 0 && dz == -1) {
                            world.setBlockState(pos, Blocks.FURNACE.getDefaultState().with(net.minecraft.block.HorizontalFacingBlock.FACING, Direction.SOUTH), 3);
                        } else if (dx == 1 && dz == -1) {
                            world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(net.minecraft.block.HorizontalFacingBlock.FACING, Direction.SOUTH), 3);
                            if (world.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
                                chest.setLootTable(LOOT_TABLE, random.nextLong());
                            }
                        } else if (dx == -1 && dz == 0) {
                            world.setBlockState(pos, Blocks.STONE_BRICKS.getDefaultState(), 3);
                        }
                    } else if (dy == 2) {
                        if (dx == -1 && dz == -1) {
                            world.setBlockState(pos, Blocks.LANTERN.getDefaultState(), 3);
                        } else if (dx == -1 && dz == 0) {
                            int randomValue = random.nextInt(100);
                            if (randomValue < 50) {
                                world.setBlockState(pos, ModBlocks.MORTAR_AND_PESTLE.getDefaultState(), 3);
                            } else if (randomValue < 70) {
                                world.setBlockState(pos, ModBlocks.OVEN.getDefaultState().with(com.hydroceder.hgbg.block.OvenBlock.FACING, Direction.EAST), 3);
                            } else if (randomValue < 80) {
                                world.setBlockState(pos, Blocks.CAMPFIRE.getDefaultState().with(net.minecraft.block.CampfireBlock.FACING, Direction.SOUTH), 3);
                            }
                        }
                    }
                }
            }
        }
        
        return true;
    }
}
