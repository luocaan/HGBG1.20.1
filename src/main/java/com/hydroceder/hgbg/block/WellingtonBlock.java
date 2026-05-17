package com.hydroceder.hgbg.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.item.ItemPlacementContext;
import com.hydroceder.hgbg.item.ModFoodComponents;

public class WellingtonBlock extends HorizontalFacingBlock {

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(
        3.0 / 16.0, 0.0, 3.0 / 16.0,
        13.0 / 16.0, 5.5 / 16.0, 13.0 / 16.0
    );

    public WellingtonBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, net.minecraft.util.math.Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        world.removeBlock(pos, false);

        world.spawnEntity(new net.minecraft.entity.ItemEntity(world,
                pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                new ItemStack(Items.BOWL)));

        player.getHungerManager().add(
            ModFoodComponents.WELLINGTON.getHunger(),
            ModFoodComponents.WELLINGTON.getSaturationModifier()
        );

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_PLAYER_BURP,
            SoundCategory.PLAYERS,
            1.0f, 1.0f);

        return ActionResult.SUCCESS;
    }
}
