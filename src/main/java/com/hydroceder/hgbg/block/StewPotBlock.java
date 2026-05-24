package com.hydroceder.hgbg.block;

import com.hydroceder.hgbg.block.entity.StewPotBlockEntity;
import com.hydroceder.hgbg.item.ModItems;
import com.hydroceder.hgbg.item.tool.BasketItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public class StewPotBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty HAS_CONTENTS = BooleanProperty.of("has_contents");
    public static final EnumProperty<PotType> POT_TYPE = EnumProperty.of("pot_type", PotType.class);

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(
        2.0 / 16.0, 0.0, 2.0 / 16.0,
        14.0 / 16.0, 9.0 / 16.0, 14.0 / 16.0
    );

    public enum PotType implements StringIdentifiable {
        ONGROUND("onground"),
        ONROD("onrod");

        private final String name;

        PotType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }

    public StewPotBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(FACING, Direction.NORTH)
            .with(HAS_CONTENTS, false)
            .with(POT_TYPE, PotType.ONGROUND));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntityTypes.STEW_POT_BLOCK_ENTITY, StewPotBlockEntity::tick);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_CONTENTS, POT_TYPE);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StewPotBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (placer != null) {
            world.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite())
                .with(POT_TYPE, detectPotType(world, pos)), 3);
        }
    }

    private PotType detectPotType(World world, BlockPos pos) {
        BlockState below = world.getBlockState(pos.down());
        if (below.isOf(Blocks.FURNACE) || below.isOf(Blocks.BLAST_FURNACE) || below.isOf(Blocks.SMOKER)) {
            return PotType.ONGROUND;
        }
        return PotType.ONROD;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof StewPotBlockEntity) {
                ((StewPotBlockEntity) be).dropItems();
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !isValidBase(neighborState)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return isValidBase(world.getBlockState(pos.down()));
    }

    private static boolean isValidBase(BlockState belowState) {
        return belowState.isOf(Blocks.FURNACE) || belowState.isOf(Blocks.BLAST_FURNACE)
            || belowState.isOf(Blocks.SMOKER) || belowState.isOf(Blocks.CAMPFIRE)
            || belowState.isOf(Blocks.SOUL_CAMPFIRE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof StewPotBlockEntity)) {
            return ActionResult.PASS;
        }

        StewPotBlockEntity potBE = (StewPotBlockEntity) blockEntity;
        ItemStack heldStack = player.getStackInHand(hand);

        if (!heldStack.isEmpty() && heldStack.getItem() instanceof BasketItem) {
            if (potBE.isCooking()) {
                return ActionResult.SUCCESS;
            }
            potBE.addBasket(heldStack);
            heldStack.decrement(1);
            world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON,
                SoundCategory.BLOCKS, 0.8f, 1.0f);
            return ActionResult.SUCCESS;
        }

        if (!heldStack.isEmpty() && heldStack.getItem() instanceof ShovelItem) {
            if (potBE.isCooking() || !potBE.hasBaskets()) {
                return ActionResult.SUCCESS;
            }
            potBE.startCooking(player.getUuid());
            world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE,
                SoundCategory.BLOCKS, 1.0f, 1.0f);
            return ActionResult.SUCCESS;
        }

        if (heldStack.isEmpty() && !potBE.isCooking()) {
            if (potBE.hasBaskets()) {
                potBE.dropItems();
                potBE.clearBaskets();
                world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                    SoundCategory.BLOCKS, 1.0f, 1.0f);
            } else {
                player.sendMessage(Text.translatable("stewpot.no_basket"), true);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
