package com.hydroceder.hgbg.block;

import com.hydroceder.hgbg.block.entity.StoveBlockEntity;
import com.hydroceder.hgbg.event.StoveEvents;
import com.hydroceder.hgbg.item.ModItems;
import com.hydroceder.hgbg.item.tool.PanItem;
import com.hydroceder.hgbg.recipe.pan_cooking.PanCookingRecipe;
import com.hydroceder.hgbg.recipe.pan_cooking.PanCookingRecipeManager;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 灶台方块类
 */
public class StoveBlock extends BlockWithEntity {
    public static final BooleanProperty HAS_PAN = BooleanProperty.of("has_pan");
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    
    private static final VoxelShape COLLISION_SHAPE = VoxelShapes.cuboid(
        2.0 / 16.0,
        0.0,
        2.0 / 16.0,
        14.0 / 16.0,
        6.0 / 16.0,
        14.0 / 16.0
    );
    
    public StoveBlock(Settings settings) {
        super(settings.luminance(state -> state.get(HAS_PAN) ? 14 : 0));
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(HAS_PAN, false));
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntityTypes.STOVE_BLOCK_ENTITY, com.hydroceder.hgbg.block.entity.StoveBlockEntity::tick);
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_PAN);
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StoveBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (placer != null) {
            world.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 3);
        }
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StoveBlockEntity) {
                ((StoveBlockEntity) blockEntity).dropItems();
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !neighborState.isSolid()) {
            world.breakBlock(pos, true);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof StoveBlockEntity)) {
            return ActionResult.PASS;
        }
        
        StoveBlockEntity stoveBlockEntity = (StoveBlockEntity) blockEntity;
        
        BlockPos belowPos = pos.down();
        BlockState belowState = world.getBlockState(belowPos);
        boolean isOnFurnace = belowState.isOf(Blocks.FURNACE) || belowState.isOf(Blocks.BLAST_FURNACE) || belowState.isOf(Blocks.SMOKER);
        
        if (!isOnFurnace) {
            return ActionResult.PASS;
        }
        
        ItemStack heldStack = player.getStackInHand(hand);
        
        if (!state.get(HAS_PAN) && heldStack.isOf(ModItems.PAN)) {
            ActionResult eventResult = StoveEvents.PLACE_PAN.invoker().onPlacePan(world, pos, player, heldStack);
            if (eventResult == ActionResult.FAIL) {
                return ActionResult.PASS;
            }
            stoveBlockEntity.setPan(heldStack.copy());
            heldStack.decrement(1);
            world.setBlockState(pos, state.with(HAS_PAN, true), 3);
            return ActionResult.SUCCESS;
        } 
        else if (state.get(HAS_PAN) && heldStack.isEmpty()) {
            if (stoveBlockEntity.isCooking()) {
                return ActionResult.SUCCESS;
            }
            ItemStack pan = stoveBlockEntity.getPan();
            if (!pan.isEmpty()) {
                player.giveItemStack(pan);
                for (ItemStack material : stoveBlockEntity.getMaterials()) {
                    if (!material.isEmpty()) {
                        net.minecraft.util.ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), material);
                    }
                }
                stoveBlockEntity.clearMaterials();
                stoveBlockEntity.setPan(ItemStack.EMPTY);
                world.setBlockState(pos, state.with(HAS_PAN, false), 3);
            }
            return ActionResult.SUCCESS;
        }
        else if (state.get(HAS_PAN) && heldStack.isOf(ModItems.SPATULA)) {
            if (!stoveBlockEntity.isCooking()) {
                if (stoveBlockEntity.getMaterials().isEmpty()) {
                    return ActionResult.SUCCESS;
                }
                
                Optional<PanCookingRecipeManager.MatchResult> matchResult = PanCookingRecipeManager.findRecipe(stoveBlockEntity.getMaterials());
                int cookTime = 200;
                boolean hasValidRecipe = matchResult.isPresent();
                if (hasValidRecipe) {
                    cookTime = matchResult.get().recipe.getCookTime();
                }
                
                stoveBlockEntity.startCooking(cookTime, hasValidRecipe, player.getUuid());
                world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
        }
        else if (state.get(HAS_PAN) && !heldStack.isEmpty() && !heldStack.isOf(ModItems.SPATULA)) {
            ItemStack singleStack = heldStack.copy();
            singleStack.setCount(1);
            ActionResult eventResult = StoveEvents.PLACE_ITEM.invoker().onPlaceItem(world, pos, player, singleStack);
            if (eventResult == ActionResult.FAIL) {
                return ActionResult.PASS;
            }
            stoveBlockEntity.addMaterial(singleStack);
            heldStack.decrement(1);
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.PASS;
    }
    
    /**
     * 处理烹饪完成后的逻辑
     */
    public static void finishCooking(World world, BlockPos pos, BlockState state, StoveBlockEntity blockEntity) {
        Optional<PanCookingRecipeManager.MatchResult> matchResult = PanCookingRecipeManager.findRecipe(blockEntity.getMaterials());
        java.util.List<ItemStack> outputs = new java.util.ArrayList<>();
        boolean hasValidRecipe = matchResult.isPresent();
        
        if (hasValidRecipe) {
            PanCookingRecipe recipe = matchResult.get().recipe;
            int scaleFactor = matchResult.get().scaleFactor;
            recipe.consumeMaterials(blockEntity.getMaterials(), scaleFactor);
            outputs.addAll(recipe.getOutputs(scaleFactor));
        } else {
            outputs.add(new ItemStack(Items.CHARCOAL));
        }
        
        ActionResult eventResult = StoveEvents.FINISH_COOKING.invoker().onFinishCooking(world, pos, hasValidRecipe, outputs);
        
        if (eventResult != ActionResult.FAIL) {
            for (ItemStack output : outputs) {
                net.minecraft.util.ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), output.copy());
            }
            
            if (hasValidRecipe) {
                ItemStack pan = blockEntity.getPan();
                if (!pan.isEmpty()) {
                    PanItem.incrementCookingCount(pan);
                    blockEntity.setPan(pan);
                }
                
                world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
        
        blockEntity.clearMaterials();
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }
    
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(HAS_PAN)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.1;
            double z = pos.getZ() + 0.5;
            
            world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
            
            if (random.nextDouble() < 0.3) {
                world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.05, 0.0);
            }
        }
    }
}
