package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MayonnaiseBottleItem extends DrinkItem {
    public MayonnaiseBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.MAYONNAISE_HUNGER)
            .saturationModifier(FoodProperties.MAYONNAISE_SATURATION)
            .build()));
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        // 赋予8秒反胃效果
        user.addStatusEffect(new StatusEffectInstance(
            StatusEffects.NAUSEA, 
            8 * 20, // 8秒
            0 // 等级I
        ));
        
        return result;
    }
}
