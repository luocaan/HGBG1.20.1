package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BaconItem extends Item {
    public BaconItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.BACON_HUNGER,
            FoodProperties.BACON_SATURATION
        ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // 先执行默认的食物食用逻辑
        ItemStack result = super.finishUsing(stack, world, user);
        
        // 在服务端执行效果赋予
        if (!world.isClient && user instanceof PlayerEntity) {
            // 3分钟 = 3 * 60 * 20 = 3600 ticks
            // 生命恢复II
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 3600, 1, false, true));
        }
        
        return result;
    }
}
