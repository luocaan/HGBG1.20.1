package com.hydroceder.hgbg.item.manager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.util.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;

/**
 * 饮品物品基类
 * 所有饮品都继承此类，提供统一的饮用效果和空瓶返回逻辑
 */
public class DrinkItem extends Item {
    public DrinkItem(Settings settings) {
        super(settings);
    }
    
    /**
     * 创建带有alwaysEdible的食物组件
     * 饮品总是可以被食用（即使不饿）
     */
    protected static FoodComponent.Builder createFoodComponent() {
        return new FoodComponent.Builder().alwaysEdible();
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }
    
    @Override
    public SoundEvent getDrinkSound() {
        return SoundEvents.ITEM_BOTTLE_EMPTY;
    }
    
    @Override
    public SoundEvent getEatSound() {
        return SoundEvents.ITEM_BOTTLE_EMPTY;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // 先执行默认的食物食用逻辑（恢复饥饿值和饱和度）
        ItemStack result = super.finishUsing(stack, world, user);
        
        // 非创造模式下返回空瓶
        if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
            ItemStack bottleStack = new ItemStack(Items.GLASS_BOTTLE);
            
            // 如果食用后结果为空（消耗了最后一个），直接返回空瓶
            if (result.isEmpty()) {
                return bottleStack;
            }
            
            // 使用Minecraft标准方法：优先放入背包，否则掉地上
            player.getInventory().offerOrDrop(bottleStack);
        }
        
        return result;
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }
}