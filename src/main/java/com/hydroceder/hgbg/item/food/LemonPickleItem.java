package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

/**
 * 柠檬泡菜物品类
 * 食用后恢复饥饿值和饱和度，返回碗
 */
public class LemonPickleItem extends Item {
    public LemonPickleItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.LEMON_PICKLE_HUNGER,
            FoodProperties.LEMON_PICKLE_SATURATION
        ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // 先执行默认的食物食用逻辑（恢复饥饿值和饱和度）
        ItemStack result = super.finishUsing(stack, world, user);
        
        // 使用统一的碗返还处理
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
}
