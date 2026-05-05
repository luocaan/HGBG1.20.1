package com.hydroceder.hgbg.block;

import com.hydroceder.hgbg.block.entity.WoodenCupBlockEntity;
import com.hydroceder.hgbg.seasoning.Seasoning;
import com.hydroceder.hgbg.seasoning.SeasoningRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class WoodenCupBlock extends Block implements BlockEntityProvider {

    public static final IntProperty CUP_COUNT = IntProperty.of("cup_count", 1, 3);

    private static final VoxelShape SHAPE_1 = VoxelShapes.cuboid(0.375, 0.0, 0.375, 0.625, 0.5, 0.625);
    private static final VoxelShape SHAPE_2 = VoxelShapes.cuboid(0.25, 0.0, 0.25, 0.75, 0.5, 0.75);
    private static final VoxelShape SHAPE_3 = VoxelShapes.cuboid(0.1875, 0.0, 0.1875, 0.8125, 0.5, 0.8125);

    public WoodenCupBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(CUP_COUNT, 1).with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CUP_COUNT, Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, net.minecraft.world.BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        int count = state.get(CUP_COUNT);
        return switch (count) {
            case 2 -> SHAPE_2;
            case 3 -> SHAPE_3;
            default -> SHAPE_1;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, net.minecraft.world.BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return getOutlineShape(state, world, pos, null);
    }

    @Override
    public WoodenCupBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WoodenCupBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        WoodenCupBlockEntity cupEntity = (WoodenCupBlockEntity) world.getBlockEntity(pos);
        if (cupEntity == null) {
            return ActionResult.PASS;
        }

        ItemStack heldStack = player.getStackInHand(hand);

        if (!heldStack.isEmpty()) {
            Seasoning heldSeasoning = SeasoningRegistry.getSeasoning(heldStack.getItem());

            if (heldSeasoning != null && heldSeasoning.isStorable()) {
                if (cupEntity.storeSeasoning(heldStack)) {
                    heldStack.decrement(1);

                    if (heldSeasoning.isBottled()) {
                        player.giveItemStack(new ItemStack(Items.GLASS_BOTTLE));
                        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    } else {
                        world.playSound(null, pos, SoundEvents.BLOCK_SAND_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    }

                    player.sendMessage(Text.translatable("block.hunger-begone.cup.stored"), true);
                    return ActionResult.SUCCESS;
                }
            } else if (heldStack.isOf(asItem())) {
                if (cupEntity.canAddCup()) {
                    heldStack.decrement(1);
                    cupEntity.setCupCount(cupEntity.getCupCount() + 1);
                    world.setBlockState(pos, state.with(CUP_COUNT, cupEntity.getCupCount()));
                    world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    player.sendMessage(Text.translatable("block.hunger-begone.wooden_cup.added"), true);
                    return ActionResult.SUCCESS;
                } else {
                    player.sendMessage(Text.translatable("block.hunger-begone.wooden_cup.full"), true);
                    return ActionResult.FAIL;
                }
            }
        }

        if (cupEntity.hasSeasoning()) {
            for (int slot = 0; slot < cupEntity.getCupCount(); slot++) {
                if (cupEntity.itemsAreEmpty(slot)) continue;

                Seasoning storedSeasoning = cupEntity.getSeasoningData(slot);
                if (storedSeasoning == null) continue;

                boolean canInteract = false;

                if (storedSeasoning.isBottled()) {
                    if (heldStack.isOf(Items.GLASS_BOTTLE)) {
                        canInteract = true;
                    } else if (heldStack.isEmpty()) {
                        player.sendMessage(Text.translatable("block.hunger-begone.cup.needs_bottle"), true);
                        return ActionResult.FAIL;
                    }
                } else {
                    if (heldStack.isEmpty()) {
                        canInteract = true;
                    }
                }

                if (canInteract) {
                    ItemStack seasoningStack = cupEntity.takeOut(slot);

                    if (storedSeasoning.isBottled() && !heldStack.isEmpty()) {
                        heldStack.decrement(1);
                    }

                    player.giveItemStack(seasoningStack);

                    if (storedSeasoning.isBottled()) {
                        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        player.sendMessage(Text.translatable("block.hunger-begone.cup.taken_bottle"), true);
                    } else {
                        world.playSound(null, pos, SoundEvents.BLOCK_SAND_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        player.sendMessage(Text.translatable("block.hunger-begone.cup.taken_hand"), true);
                    }
                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    private void dropItem(World world, BlockPos pos, Item item) {
        net.minecraft.entity.ItemEntity itemEntity = new net.minecraft.entity.ItemEntity(
            world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            new ItemStack(item)
        );
        world.spawnEntity(itemEntity);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            WoodenCupBlockEntity cupEntity = (WoodenCupBlockEntity) world.getBlockEntity(pos);
            if (cupEntity != null) {
                for (int i = 0; i < cupEntity.size(); i++) {
                    ItemStack stack = cupEntity.getStack(i);
                    if (!stack.isEmpty()) {
                        net.minecraft.entity.ItemEntity itemEntity = new net.minecraft.entity.ItemEntity(
                            world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            stack.copy()
                        );
                        world.spawnEntity(itemEntity);
                    }
                }

                for (int i = 1; i < cupEntity.getCupCount(); i++) {
                    dropItem(world, pos, asItem());
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
