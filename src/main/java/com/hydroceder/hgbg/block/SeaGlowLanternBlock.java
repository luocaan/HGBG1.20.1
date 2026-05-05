package com.hydroceder.hgbg.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SeaGlowLanternBlock extends Block implements Waterloggable {

    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty HANGING = Properties.HANGING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape STANDING_SHAPE = VoxelShapes.cuboid(
            4.0 / 16.0, 0.0, 4.0 / 16.0,
            12.0 / 16.0, 8.0 / 16.0, 12.0 / 16.0
    );
    private static final VoxelShape HANGING_SHAPE = VoxelShapes.cuboid(
            4.0 / 16.0, 0.0, 4.0 / 16.0,
            12.0 / 16.0, 16.0 / 16.0, 12.0 / 16.0
    );

    public SeaGlowLanternBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(LIT, false)
                .with(HANGING, false)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, HANGING, WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return state.get(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, net.minecraft.world.WorldView world, BlockPos pos) {
        if (state.get(HANGING)) {
            BlockPos above = pos.up();
            BlockState aboveState = world.getBlockState(above);
            return aboveState.isSideSolidFullSquare(world, above, Direction.DOWN);
        } else {
            BlockPos below = pos.down();
            BlockState belowState = world.getBlockState(below);
            return !belowState.isAir() && belowState.getCollisionShape(world, below).getMax(Direction.Axis.Y) >= 1.0;
        }
    }

    private static Direction getConnectedDirection(BlockState state) {
        return state.get(HANGING) ? Direction.UP : Direction.DOWN;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());

        for (Direction direction : ctx.getPlacementDirections()) {
            if (direction == Direction.UP) {
                BlockState blockState = this.getDefaultState().with(HANGING, true).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
                if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
                    return blockState;
                }
            } else if (direction == Direction.DOWN) {
                BlockState blockState = this.getDefaultState().with(HANGING, false).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
                if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
                    return blockState;
                }
            }
        }

        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == getConnectedDirection(state) && !state.canPlaceAt(world, pos)) {
            return net.minecraft.block.Blocks.AIR.getDefaultState();
        }

        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        boolean powered = world.isReceivingRedstonePower(pos);
        if (powered != state.get(LIT)) {
            return state.with(LIT, powered);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }
}
