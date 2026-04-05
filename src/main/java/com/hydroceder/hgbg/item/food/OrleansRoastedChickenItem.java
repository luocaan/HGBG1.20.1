package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

/**
 * 奥尔良烤鸡物品类
 * 食用后恢复10点饥饿值，14点饱和度
 * 食用后返还碗
 * 不可堆叠
 */
public class OrleansRoastedChickenItem extends Item {
    public OrleansRoastedChickenItem(Settings settings) {
        super(settings.maxCount(1) // 不可堆叠
            .food(FoodProperties.createAlwaysEdibleFood(
                FoodProperties.ORLEANS_ROASTED_CHICKEN_HUNGER,
                FoodProperties.ORLEANS_ROASTED_CHICKEN_SATURATION
            ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // 先执行默认的食物食用逻辑
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
        return 32; // 食用时间
    }
}
