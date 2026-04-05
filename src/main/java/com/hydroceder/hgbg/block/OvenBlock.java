package com.hydroceder.hgbg.block;

import com.hydroceder.hgbg.HgbgMod;
import com.hydroceder.hgbg.block.entity.OvenBlockEntity;
import com.hydroceder.hgbg.event.OvenEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * 烤箱方块类
 * 支持存储3个物品，具有4种视觉状态
 */
public class OvenBlock extends BlockWithEntity {
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final IntProperty ITEM_COUNT = IntProperty.of("item_count", 0, 3);
    public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
    
    // 碰撞箱：高度9像素(9/16)，前后向内缩3像素(3/16)
    // 由于需要根据朝向旋转，定义基础形状（朝北）
    // 前后缩进3像素意味着Z方向从0到13/16（前面缩进）或从3/16到1（后面缩进）
    // 根据模型结构，烤箱前面是Z=2到Z=13的区域，所以碰撞箱应该是前后各缩3像素
    // 基础形状：X: 0-1, Y: 0-9/16, Z: 3/16-1 (朝北时，前面缩进)
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.cuboid(0.0, 0.0, 0.1875, 1.0, 0.5625, 1.0);
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5625, 0.8125);
    private static final VoxelShape EAST_SHAPE = VoxelShapes.cuboid(0.0, 0.0, 0.0, 0.8125, 0.5625, 1.0);
    private static final VoxelShape WEST_SHAPE = VoxelShapes.cuboid(0.1875, 0.0, 0.0, 1.0, 0.5625, 1.0);
    
    public OvenBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(OPEN, false)
            .with(ITEM_COUNT, 0)
            .with(FACING, Direction.NORTH));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN, ITEM_COUNT, FACING);
    }
    
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OvenBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntityTypes.OVEN_BLOCK_ENTITY, OvenBlockEntity::tick);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof OvenBlockEntity)) {
            return ActionResult.PASS;
        }
        
        OvenBlockEntity ovenEntity = (OvenBlockEntity) blockEntity;
        
        boolean isOpen = state.get(OPEN);
        int itemCount = state.get(ITEM_COUNT);
        ItemStack heldStack = player.getStackInHand(hand);
        
        // 满_关状态（正在烤制）不响应交互
        if (!isOpen && itemCount > 0) {
            return ActionResult.PASS;
        }
        
        // 空手交互
        if (heldStack.isEmpty()) {
            // 空_关 → 空_开
            if (!isOpen && itemCount == 0) {
                ActionResult eventResult = OvenEvents.OPEN.invoker().onOvenDoor(world, pos, player);
                if (eventResult == ActionResult.FAIL) {
                    return ActionResult.PASS;
                }
                world.setBlockState(pos, state.with(OPEN, true), 3);
                return ActionResult.SUCCESS;
            }
            // 空_开 → 空_关
            else if (isOpen && itemCount == 0) {
                ActionResult eventResult = OvenEvents.CLOSE.invoker().onOvenDoor(world, pos, player);
                if (eventResult == ActionResult.FAIL) {
                    return ActionResult.PASS;
                }
                world.setBlockState(pos, state.with(OPEN, false), 3);
                return ActionResult.SUCCESS;
            }
            // 满_开 → 满_关（开始烤制）
            else if (isOpen && itemCount > 0) {
                ActionResult eventResult = OvenEvents.CLOSE.invoker().onOvenDoor(world, pos, player);
                if (eventResult == ActionResult.FAIL) {
                    return ActionResult.PASS;
                }
                world.setBlockState(pos, state.with(OPEN, false), 3);
                ovenEntity.startCooking();
                return ActionResult.SUCCESS;
            }
        }
        // 手持物品交互
        else {
            // 空_关 + 手持物品 → 开门
            if (!isOpen && itemCount == 0) {
                ActionResult eventResult = OvenEvents.OPEN.invoker().onOvenDoor(world, pos, player);
                if (eventResult == ActionResult.FAIL) {
                    return ActionResult.PASS;
                }
                world.setBlockState(pos, state.with(OPEN, true), 3);
                return ActionResult.SUCCESS;
            }
            // 满_开 + 手持物品 → 关门开始烤制
            else if (isOpen && itemCount >= 3) {
                ActionResult eventResult = OvenEvents.CLOSE.invoker().onOvenDoor(world, pos, player);
                if (eventResult == ActionResult.FAIL) {
                    return ActionResult.PASS;
                }
                world.setBlockState(pos, state.with(OPEN, false), 3);
                ovenEntity.startCooking();
                return ActionResult.SUCCESS;
            }
            // 开状态下可以放入物品
            else if (isOpen && itemCount < 3) {
                for (int i = 0; i < 3; i++) {
                    if (ovenEntity.getItem(i).isEmpty()) {
                        // 只放入1个物品，而不是整个堆叠
                        ItemStack singleItem = heldStack.copy();
                        singleItem.setCount(1);
                        ovenEntity.setItem(i, singleItem);
                        heldStack.decrement(1);
                        
                        int newCount = itemCount + 1;
                        world.setBlockState(pos, state.with(ITEM_COUNT, newCount), 3);
                        
                        HgbgMod.debugLog(player, "放入物品: " + singleItem.getName().getString() + ", 当前物品数: " + newCount);
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        
        return ActionResult.PASS;
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShapeForDirection(state.get(FACING));
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShapeForDirection(state.get(FACING));
    }
    
    private VoxelShape getShapeForDirection(Direction direction) {
        switch (direction) {
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return NORTH_SHAPE;
        }
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof OvenBlockEntity) {
                ((OvenBlockEntity) blockEntity).dropItems();
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
