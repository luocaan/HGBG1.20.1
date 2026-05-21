package com.hydroceder.hgbg.structure;

import com.hydroceder.hgbg.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.List;

public class SmallGardenFeature extends Feature<DefaultFeatureConfig> {

    private static final int MIN_Y = -64;

    public SmallGardenFeature() {
        super(DefaultFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        List<CropBlock> crops = ModBlocks.getHgbgCrops();
        if (crops.isEmpty()) {
            return false;
        }

        BlockPos surfacePos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, origin).down();

        if (surfacePos.getY() < MIN_Y) {
            return false;
        }

        Block surfaceBlock = world.getBlockState(surfacePos).getBlock();
        if (!canReplaceSurface(surfaceBlock)) {
            return false;
        }

        BlockPos farmlandPos0 = surfacePos.add(0, 0, 0);
        BlockPos waterPos = surfacePos.add(1, 0, 0);
        BlockPos farmlandPos1 = surfacePos.add(0, 0, 1);

        if (!isAirOrReplaceable(world, waterPos)) {
            return false;
        }
        if (!isAirOrReplaceable(world, farmlandPos0.up())) {
            return false;
        }
        if (!isAirOrReplaceable(world, farmlandPos1.up())) {
            return false;
        }

        world.setBlockState(farmlandPos0, Blocks.FARMLAND.getDefaultState(), 3);
        world.setBlockState(waterPos, Blocks.WATER.getDefaultState(), 3);
        world.setBlockState(farmlandPos1, Blocks.FARMLAND.getDefaultState(), 3);

        CropBlock chosenCrop = crops.get(random.nextInt(crops.size()));
        int maxAge = chosenCrop.getMaxAge();

        world.setBlockState(farmlandPos0.up(), chosenCrop.withAge(maxAge), 3);
        world.setBlockState(farmlandPos1.up(), chosenCrop.withAge(maxAge), 3);

        return true;
    }

    private static boolean canReplaceSurface(Block block) {
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT ||
               block == Blocks.PODZOL || block == Blocks.FARMLAND ||
               block == Blocks.COARSE_DIRT || block == Blocks.ROOTED_DIRT;
    }

    private static boolean isAirOrReplaceable(StructureWorldAccess world, BlockPos pos) {
        return world.isAir(pos);
    }
}