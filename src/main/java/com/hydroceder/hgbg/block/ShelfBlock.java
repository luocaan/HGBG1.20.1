package com.hydroceder.hgbg.block;

import com.hydroceder.hgbg.block.entity.ShelfBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShelfBlock extends BlockWithEntity {
    public ShelfBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShelfBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ShelfBlockEntity)) {
            return ActionResult.PASS;
        }
        
        ShelfBlockEntity shelfEntity = (ShelfBlockEntity) blockEntity;
        ItemStack heldStack = player.getStackInHand(hand);
        
        if (!heldStack.isEmpty()) {
            if (shelfEntity.addItem(heldStack)) {
                heldStack.decrement(1);
                player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1.0f, 1.0f);
                shelfEntity.markDirty();
                shelfEntity.updateHint(player);
                return ActionResult.SUCCESS;
            }
        } else {
            if (player.isSneaking()) {
                shelfEntity.dropItems(world, pos);
                shelfEntity.clearAll();
                shelfEntity.markDirty();
                return ActionResult.SUCCESS;
            } else {
                ItemStack removedItem = shelfEntity.removeLast();
                if (!removedItem.isEmpty()) {
                    if (!player.getInventory().insertStack(removedItem)) {
                        player.dropItem(removedItem, false);
                    }
                    player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1.0f, 1.0f);
                    shelfEntity.markDirty();
                    shelfEntity.updateHint(player);
                    return ActionResult.SUCCESS;
                }
            }
        }
        
        return ActionResult.PASS;
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ShelfBlockEntity) {
                ((ShelfBlockEntity) blockEntity).dropItems(world, pos);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
