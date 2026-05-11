package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.block.ModBlocks;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CreamyMushroomSoupItem extends BlockItem {
    public CreamyMushroomSoupItem(Block block, Settings settings) {
        super(block, settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.CREAMY_MUSHROOM_SOUP_HUNGER,
            FoodProperties.CREAMY_MUSHROOM_SOUP_SATURATION
        ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        if (!world.isClient && user instanceof PlayerEntity) {
            for (StatusEffectInstance effect : new java.util.ArrayList<>(user.getStatusEffects())) {
                user.removeStatusEffect(effect.getEffectType());
            }
        }
        
        return FoodProperties.handleBowlReturn(stack, world, user, result);
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
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
        
        if (blockState.canReplace(new ItemPlacementContext(context))) {
            if (placeBlockAt(context, blockPos, context.getSide())) {
                return ActionResult.SUCCESS;
            }
            
            BlockPos adjacentPos = blockPos.offset(context.getSide());
            if (placeBlockAt(context, adjacentPos, context.getSide().getOpposite())) {
                return ActionResult.SUCCESS;
            }
        } else {
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
        
        if (!world.isAir(pos) && !world.getBlockState(pos).canReplace(new ItemPlacementContext(context))) {
            return false;
        }
        
        if (!world.setBlockState(pos, ModBlocks.CREAMY_MUSHROOM_SOUP_BLOCK.getDefaultState(), 11)) {
            return false;
        }
        
        if (player != null && !player.isCreative()) {
            stack.decrement(1);
        }
        
        world.playSound(null, pos, SoundEvents.BLOCK_WOOL_PLACE,
                       player == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS,
                       1.0f, 1.0f);
        
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, net.minecraft.world.World world, java.util.List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.hunger-begone.placeable.tooltip").formatted(net.minecraft.util.Formatting.GRAY));
    }
}
