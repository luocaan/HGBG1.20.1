package com.hydroceder.hgbg.block;

import com.hydroceder.hgbg.block.entity.CoinOperatedMachineBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Equipment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.item.Item;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.Identifier;

public class CoinOperatedMachineBlock extends HorizontalFacingBlock implements BlockEntityProvider, Equipment {

    public static final IntProperty COIN_COUNT = IntProperty.of("coin_count", 0, 6);

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(
        4.0 / 16.0, 0.0, 4.0 / 16.0,
        12.0 / 16.0, 8.0 / 16.0, 12.0 / 16.0
    );

    public CoinOperatedMachineBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(FACING, net.minecraft.util.math.Direction.NORTH)
            .with(COIN_COUNT, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, COIN_COUNT);
    }

    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, net.minecraft.world.BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, net.minecraft.world.BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        if (world.getBlockEntity(pos) instanceof CoinOperatedMachineBlockEntity entity) {
            if (!entity.canInteract()) {
                return ActionResult.PASS;
            }
        } else {
            return ActionResult.PASS;
        }

        ItemStack heldStack = player.getStackInHand(hand);

        if (heldStack.isOf(Items.IRON_NUGGET)) {
            CoinOperatedMachineBlockEntity blockEntity = (CoinOperatedMachineBlockEntity) world.getBlockEntity(pos);
            
            if (blockEntity != null && !blockEntity.isPlayingMusic()) {
                if (!player.isCreative()) {
                    heldStack.decrement(1);
                }

                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.BLOCK_NOTE_BLOCK_HARP.value(),
                    SoundCategory.BLOCKS,
                    1.0f, 0.8f);

                blockEntity.startMusicPlayback();

                return ActionResult.SUCCESS;
            } else if (blockEntity != null && blockEntity.isPlayingMusic()) {
                return ActionResult.PASS;
            }
        }

        if (heldStack.isOf(Items.GOLD_NUGGET)) {
            int currentCount = state.get(COIN_COUNT);
            int requiredCount = world.getBlockEntity(pos) instanceof CoinOperatedMachineBlockEntity entity2 ? entity2.getRequiredCoins() : 3;
            int newCount = Math.min(currentCount + 1, 6);

            if (!player.isCreative()) {
                heldStack.decrement(1);
            }

            world.setBlockState(pos, state.with(COIN_COUNT, newCount));

            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                SoundCategory.BLOCKS,
                1.0f, 0.5f);

            if (newCount >= requiredCount) {
                ejectMusicDisc(world, pos);
                
                if (world.getBlockEntity(pos) instanceof CoinOperatedMachineBlockEntity entity3) {
                    entity3.recordEjectTime();
                }
                
                world.setBlockState(pos, state.with(COIN_COUNT, 0));
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private void ejectMusicDisc(World world, BlockPos pos) {
        TagKey<Item> musicDiscTag = TagKey.of(Registries.ITEM.getKey(), new Identifier("minecraft", "music_discs"));
        java.util.List<Item> musicDiscs = new java.util.ArrayList<>();

        for (Item item : Registries.ITEM) {
            try {
                if (item.getDefaultStack().isIn(musicDiscTag)) {
                    musicDiscs.add(item);
                }
            } catch (Exception e) {
                continue;
            }
        }

        if (!musicDiscs.isEmpty()) {
            Random random = world.getRandom();
            Item discItem = musicDiscs.get(random.nextInt(musicDiscs.size()));
            ItemStack discStack = new ItemStack(discItem);

            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;

            ItemEntity itemEntity = new ItemEntity(world, x, y, z, discStack);
            world.spawnEntity(itemEntity);

            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                SoundCategory.BLOCKS,
                1.0f, 0.5f);
        } else {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                SoundCategory.BLOCKS,
                1.0f, 0.5f);
        }
    }

    @Override
    public net.minecraft.block.entity.BlockEntity createBlockEntity(net.minecraft.util.math.BlockPos pos, BlockState state) {
        return new CoinOperatedMachineBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof CoinOperatedMachineBlockEntity entity) {
                entity.stopMusicPlayback();
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public net.minecraft.sound.SoundEvent getEquipSound() {
        return SoundEvents.BLOCK_WOOD_PLACE;
    }
}
