package com.hydroceder.hgbg.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

/**
 * 自定义棕榈树苗 - 可种植在沙子上
 */
public class CoconutSaplingBlock extends SaplingBlock {
    public CoconutSaplingBlock(SaplingGenerator generator, Settings settings) {
        super(generator, settings);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos)
                || floor.isOf(Blocks.SAND)
                || floor.isOf(Blocks.RED_SAND);
    }
}
