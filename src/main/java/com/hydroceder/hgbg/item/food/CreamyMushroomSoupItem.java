package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

/**
 * 奶油蘑菇汤物品类
 * 食用后回复饥饿值7点，饱和度10点
 * 去除所有药水效果
 */
public class CreamyMushroomSoupItem extends Item {
    public CreamyMushroomSoupItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.CREAMY_MUSHROOM_SOUP_HUNGER,
            FoodProperties.CREAMY_MUSHROOM_SOUP_SATURATION
        ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // 先执行默认的食物食用逻辑
        ItemStack result = super.finishUsing(stack, world, user);
        
        // 在服务端执行效果清除
        if (!world.isClient && user instanceof PlayerEntity) {
            // 清除所有药水效果（先复制到新列表，避免ConcurrentModificationException）
            for (StatusEffectInstance effect : new java.util.ArrayList<>(user.getStatusEffects())) {
                user.removeStatusEffect(effect.getEffectType());
            }
        }
        
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
