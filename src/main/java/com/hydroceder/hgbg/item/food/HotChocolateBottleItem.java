package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class HotChocolateBottleItem extends DrinkItem {
    public HotChocolateBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.HOT_CHOCOLATE_HUNGER)
            .saturationModifier(FoodProperties.HOT_CHOCOLATE_SATURATION)
            .build()));
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        // 赋予1分钟生命恢复II
        user.addStatusEffect(new StatusEffectInstance(
            StatusEffects.REGENERATION, 
            60 * 20, // 1分钟
            1 // 等级II
        ));
        
        return result;
    }
}
