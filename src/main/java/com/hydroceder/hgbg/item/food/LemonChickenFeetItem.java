package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

/**
 * 柠檬鸡爪物品类
 * 食用后恢复6点饥饿值，12点饱和度
 * 食用后返还碗
 * 不可堆叠
 */
public class LemonChickenFeetItem extends Item {
    public LemonChickenFeetItem(Settings settings) {
        super(settings.maxCount(1)
            .food(FoodProperties.createAlwaysEdibleFood(
                FoodProperties.LEMON_CHICKEN_FEET_HUNGER,
                FoodProperties.LEMON_CHICKEN_FEET_SATURATION
            ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
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
