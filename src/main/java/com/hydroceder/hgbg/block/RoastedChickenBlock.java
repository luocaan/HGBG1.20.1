package com.hydroceder.hgbg.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
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
import com.hydroceder.hgbg.item.ModFoodComponents;

/**
 * 烤鸡方块类
 * 支持多次交互食用，每次交互切换模型并恢复饥饿值
 */
public class RoastedChickenBlock extends Block {
    public static final IntProperty BITE_COUNT = IntProperty.of("bite_count", 0, 4);
    public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Type.HORIZONTAL);
    
    // 碰撞箱：根据模型尺寸设置（高度6像素，深度13像素）
    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0.0, 0.0, 0.125, 1.0, 0.375, 0.9375);
    
    public RoastedChickenBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(BITE_COUNT, 0)
                .with(FACING, Direction.NORTH));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BITE_COUNT, FACING);
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
        
        int currentBites = state.get(BITE_COUNT);
        
        if (currentBites < 4) {
            // 还有剩余部分，切换到下一个状态
            int nextBites = currentBites + 1;
            world.setBlockState(pos, state.with(BITE_COUNT, nextBites), 3);
            
            // 播放食用音效
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_GENERIC_EAT,
                SoundCategory.PLAYERS,
                1.0f, 1.0f);
        } else {
            // 最后一块，移除方块并掉落碗
            world.removeBlock(pos, false);
            
            ItemEntity bowlEntity = new ItemEntity(
                world, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5,
                new net.minecraft.item.ItemStack(Items.BOWL));
            world.spawnEntity(bowlEntity);
            
            // 播放食用音效和打嗝音效
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_GENERIC_EAT,
                SoundCategory.PLAYERS,
                1.0f, 1.0f);
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_PLAYER_BURP,
                SoundCategory.PLAYERS,
                1.0f, 0.8f); // 稍微低一点的音调
        }
        
        // 恢复饥饿值和饱和度
        player.getHungerManager().add(
            ModFoodComponents.ORLEANS_ROASTED_CHICKEN.getHunger(),
            ModFoodComponents.ORLEANS_ROASTED_CHICKEN.getSaturationModifier()
        );
        
        return ActionResult.SUCCESS;
    }
}