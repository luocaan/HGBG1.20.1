package com.hydroceder.hgbg.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class EggplantCropBlock extends CropBlock {

    private ItemConvertible seedsItem;

    private static final VoxelShape SHAPE_STAGE0 = createCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);
    private static final VoxelShape SHAPE_STAGE1 = createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private static final VoxelShape SHAPE_STAGE2 = createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

    public EggplantCropBlock(ItemConvertible seeds) {
        super(FabricBlockSettings.copyOf(Blocks.WHEAT));
        this.seedsItem = seeds;
    }

    public void setSeedsItem(ItemConvertible seeds) {
        this.seedsItem = seeds;
    }

    @Override
    public int getMaxAge() {
        return 2;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        int age = this.getAge(state);
        switch (age) {
            case 0: return SHAPE_STAGE0;
            case 1: return SHAPE_STAGE1;
            default: return SHAPE_STAGE2;
        }
    }

    @Override
    public void grow(net.minecraft.server.world.ServerWorld world, net.minecraft.util.math.random.Random random, BlockPos pos, BlockState state) {
        int currentAge = this.getAge(state);
        if (currentAge < this.getMaxAge()) {
            world.setBlockState(pos, this.withAge(currentAge + 1), 2);
        }
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        if (seedsItem == null) {
            return this;
        }
        return seedsItem;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}