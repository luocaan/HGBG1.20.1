package com.hydroceder.hgbg.block;

import com.hydroceder.hgbg.block.entity.CupBlockEntity;
import com.hydroceder.hgbg.seasoning.Seasoning;
import com.hydroceder.hgbg.seasoning.SeasoningRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class EmptyCupBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty HAS_SEASONING = BooleanProperty.of("has_seasoning");

    private static final VoxelShape CUP_SHAPE = VoxelShapes.cuboid(0.375, 0.0, 0.375, 0.625, 0.28125, 0.625);

    public EmptyCupBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HAS_SEASONING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_SEASONING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, net.minecraft.world.BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return CUP_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, net.minecraft.world.BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return CUP_SHAPE;
    }

    @Override
    public CupBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CupBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        CupBlockEntity cupEntity = (CupBlockEntity) world.getBlockEntity(pos);
        if (cupEntity == null) {
            return ActionResult.PASS;
        }

        ItemStack heldStack = player.getStackInHand(hand);

        if (!heldStack.isEmpty()) {
            Seasoning heldSeasoning = SeasoningRegistry.getSeasoning(heldStack.getItem());
            if (heldSeasoning != null && heldSeasoning.isStorable()) {
                boolean wasEmpty = !cupEntity.hasSeasoning();
                if (cupEntity.storeSeasoning(heldStack)) {
                    heldStack.decrement(1);

                    if (wasEmpty) {
                        world.setBlockState(pos, state.with(HAS_SEASONING, true));
                    }

                    if (heldSeasoning.isBottled()) {
                        player.giveItemStack(new ItemStack(Items.GLASS_BOTTLE));
                        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    } else {
                        world.playSound(null, pos, SoundEvents.BLOCK_SAND_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    }

                    player.sendMessage(Text.translatable("block.hunger-begone.cup.stored"), true);
                    return ActionResult.SUCCESS;
                }
            }
        }

        if (cupEntity.hasSeasoning()) {
            Seasoning storedSeasoning = cupEntity.getSeasoningData();

            if (storedSeasoning != null && storedSeasoning.isBottled()) {
                if (heldStack.isOf(Items.GLASS_BOTTLE)) {
                    ItemStack seasoningStack = cupEntity.takeOut(player);
                    heldStack.decrement(1);

                    if (!cupEntity.hasSeasoning()) {
                        world.setBlockState(pos, state.with(HAS_SEASONING, false));
                    }

                    player.giveItemStack(seasoningStack);
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    player.sendMessage(Text.translatable("block.hunger-begone.cup.taken_bottle"), true);
                    return ActionResult.SUCCESS;
                } else if (heldStack.isEmpty()) {
                    player.sendMessage(Text.translatable("block.hunger-begone.cup.needs_bottle"), true);
                    return ActionResult.FAIL;
                }
            } else {
                if (heldStack.isEmpty()) {
                    ItemStack seasoningStack = cupEntity.takeOut(player);

                    if (!cupEntity.hasSeasoning()) {
                        world.setBlockState(pos, state.with(HAS_SEASONING, false));
                    }

                    player.giveItemStack(seasoningStack);
                    world.playSound(null, pos, SoundEvents.BLOCK_SAND_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    player.sendMessage(Text.translatable("block.hunger-begone.cup.taken_hand"), true);
                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            CupBlockEntity cupEntity = (CupBlockEntity) world.getBlockEntity(pos);
            if (cupEntity != null && cupEntity.hasSeasoning()) {
                ItemStack drop = cupEntity.getStoredSeasoning().copy();
                net.minecraft.entity.ItemEntity itemEntity = new net.minecraft.entity.ItemEntity(
                    world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop
                );
                world.spawnEntity(itemEntity);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}