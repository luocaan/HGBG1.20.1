package com.hydroceder.hgbg.block;

import com.hydroceder.hgbg.block.entity.StewPotBlockEntity;
import com.hydroceder.hgbg.item.tool.PotLidItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.text.Text;
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
    public static final EnumProperty<StewState> STEW_STATE = EnumProperty.of("stew_state", StewState.class);
    public static final EnumProperty<PotType> POT_TYPE = EnumProperty.of("pot_type", PotType.class);

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(
        2.0 / 16.0, 0.0, 2.0 / 16.0,
        14.0 / 16.0, 9.0 / 16.0, 14.0 / 16.0
    );

    public enum StewState implements StringIdentifiable {
        EMPTY("empty"),
        HAS_WATER("has_water"),
        COOKING("cooking");

        private final String name;

        StewState(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }

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
            .with(STEW_STATE, StewState.EMPTY)
            .with(POT_TYPE, PotType.ONGROUND));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntityTypes.STEW_POT_BLOCK_ENTITY, StewPotBlockEntity::tick);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, STEW_STATE, POT_TYPE);
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
            world.setBlockState(pos, state
                .with(FACING, placer.getHorizontalFacing().getOpposite())
                .with(POT_TYPE, detectPotType(world, pos))
                .with(STEW_STATE, StewState.EMPTY), 3);
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
        StewState currentState = state.get(STEW_STATE);

        // 1. 水桶 → EMPTY 状态加水
        if (!heldStack.isEmpty() && heldStack.isOf(Items.WATER_BUCKET) && currentState == StewState.EMPTY) {
            if (!player.isCreative()) {
                heldStack.decrement(1);
                player.giveItemStack(new ItemStack(Items.BUCKET));
            }
            world.setBlockState(pos, state.with(STEW_STATE, StewState.HAS_WATER), 3);
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY,
                SoundCategory.BLOCKS, 1.0f, 1.0f);
            for (int i = 0; i < 10; i++) {
                double dx = world.random.nextGaussian() * 0.1;
                double dy = world.random.nextDouble() * 0.2 + 0.1;
                double dz = world.random.nextGaussian() * 0.1;
                world.addParticle(ParticleTypes.SPLASH,
                    pos.getX() + 0.5 + dx, pos.getY() + 0.7, pos.getZ() + 0.5 + dz,
                    0.0, 0.0, 0.0);
            }
            return ActionResult.SUCCESS;
        }

        // 2. 放入材料 → HAS_WATER 状态（非水桶非锅盖）
        if (!heldStack.isEmpty()
            && !heldStack.isOf(Items.WATER_BUCKET)
            && !(heldStack.getItem() instanceof PotLidItem)
            && currentState == StewState.HAS_WATER) {
            ItemStack singleItem = heldStack.copy();
            singleItem.setCount(1);
            if (potBE.addMaterial(singleItem)) {
                heldStack.decrement(1);
                world.playSound(null, pos, SoundEvents.BLOCK_WATER_AMBIENT,
                    SoundCategory.BLOCKS, 0.5f, 1.0f);
                return ActionResult.SUCCESS;
            }
        }

        // 3. 锅盖 → HAS_WATER 状态开始烹饪
        if (!heldStack.isEmpty() && heldStack.getItem() instanceof PotLidItem && currentState == StewState.HAS_WATER) {
            potBE.addLid();
            heldStack.decrement(1);
            world.setBlockState(pos, state.with(STEW_STATE, StewState.COOKING), 3);
            potBE.startCooking(player.getUuid());
            world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE,
                SoundCategory.BLOCKS, 1.0f, 1.0f);
            return ActionResult.SUCCESS;
        }

        // 4. 空手 + EMPTY + 有材料 → 取出所有
        if (heldStack.isEmpty() && currentState == StewState.EMPTY && potBE.hasMaterialsOrLid()) {
            potBE.dropItems();
            potBE.clearMaterials();
            world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                SoundCategory.BLOCKS, 1.0f, 1.0f);
            return ActionResult.SUCCESS;
        }

        // 5. 烹饪中 → 不可操作
        if (currentState == StewState.COOKING) {
            return ActionResult.SUCCESS;
        }

        // 6. 手持物品 + 无水 → 提示
        if (!heldStack.isEmpty() && currentState == StewState.EMPTY) {
            player.sendMessage(Text.translatable("block.hunger-begone.stew_pot.no_water"), true);
            return ActionResult.FAIL;
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

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) return;
        if (!(entity instanceof ItemEntity itemEntity)) return;
        if (state.get(STEW_STATE) != StewState.HAS_WATER) return;

        ItemStack droppedStack = itemEntity.getStack();
        if (droppedStack.isEmpty()) return;
        if (droppedStack.isOf(Items.WATER_BUCKET)) return;
        if (droppedStack.getItem() instanceof PotLidItem) return;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof StewPotBlockEntity potBE)) return;

        ItemStack singleItem = droppedStack.copy();
        singleItem.setCount(1);
        if (potBE.addMaterial(singleItem)) {
            droppedStack.decrement(1);
            if (droppedStack.isEmpty()) {
                itemEntity.discard();
            }
            world.playSound(null, pos, SoundEvents.BLOCK_WATER_AMBIENT,
                SoundCategory.BLOCKS, 0.5f, 1.0f);
        }
    }
}
