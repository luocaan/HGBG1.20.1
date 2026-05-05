package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * 奥尔良烤鸡物品类
 * 用于放置烤鸡方块，不再作为可食用物品
 */
public class OrleansRoastedChickenItem extends Item {
    public OrleansRoastedChickenItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        
        PlayerEntity player = context.getPlayer();
        if (player == null || !world.canPlayerModifyAt(player, blockPos)) {
            return ActionResult.PASS;
        }
        
        BlockState blockState = world.getBlockState(blockPos);
        
        // 检查目标位置是否可以替换（如草、雪等）
        if (blockState.canReplace(new ItemPlacementContext(context))) {
            // 尝试在目标位置放置
            if (placeBlockAt(context, blockPos, context.getSide())) {
                return ActionResult.SUCCESS;
            }
            
            // 如果失败，尝试在相邻位置放置
            BlockPos adjacentPos = blockPos.offset(context.getSide());
            if (placeBlockAt(context, adjacentPos, context.getSide().getOpposite())) {
                return ActionResult.SUCCESS;
            }
        } else {
            // 目标位置不可替换，尝试在上方或相邻位置放置
            BlockPos abovePos = blockPos.up();
            if (placeBlockAt(context, abovePos, Direction.DOWN)) {
                return ActionResult.SUCCESS;
            }
        }
        
        return ActionResult.PASS;
    }
    
    private boolean placeBlockAt(ItemUsageContext context, BlockPos pos, Direction side) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        
        // 检查位置是否可以放置
        if (!world.isAir(pos) && !world.getBlockState(pos).canReplace(new ItemPlacementContext(context))) {
            return false;
        }
        
        // 放置方块（根据玩家朝向设置方向）
        Direction facing = player != null ? player.getHorizontalFacing() : Direction.NORTH;
        if (!world.setBlockState(pos, ModBlocks.ROASTED_CHICKEN.getDefaultState()
                .with(com.hydroceder.hgbg.block.RoastedChickenBlock.FACING, facing), 11)) {
            return false;
        }
        
        // 消耗物品
        if (player != null && !player.isCreative()) {
            stack.decrement(1);
        }
        
        // 播放放置音效（使用羊毛音效）
        world.playSound(null, pos, SoundEvents.BLOCK_WOOL_PLACE,
                       player == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS,
                       1.0f, 1.0f);
        
        return true;
    }
}