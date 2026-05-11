package com.hydroceder.hgbg.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class CreamyMushroomSoupBlock extends Block {

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(
        3.0 / 16.0, 0.0, 3.0 / 16.0,
        13.0 / 16.0, 5.5 / 16.0, 13.0 / 16.0
    );

    public CreamyMushroomSoupBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return SHAPE;
    }
}
