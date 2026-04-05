package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 镶钻曲奇物品类
 * 提供8点饥饿值，16点饱和度
 * 食用后获得4分钟生命恢复II和4分钟抗性提升II
 */
public class DiamondCookieItem extends Item {
    public DiamondCookieItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.DIAMOND_COOKIE_HUNGER,
            FoodProperties.DIAMOND_COOKIE_SATURATION
        ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        if (!world.isClient && user instanceof PlayerEntity) {
            // 4分钟 = 4 * 60 * 20 = 4800 ticks
            // 生命恢复II
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 4800, 1, false, true));
            // 抗性提升II
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 4800, 1, false, true));
        }
        
        return result;
    }
}
