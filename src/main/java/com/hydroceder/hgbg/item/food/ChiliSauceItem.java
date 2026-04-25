package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ChiliSauceItem extends DrinkItem {
    public ChiliSauceItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.CHILI_SAUCE_HUNGER)
            .saturationModifier(FoodProperties.CHILI_SAUCE_SATURATION)
            .build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        if (!world.isClient && user instanceof PlayerEntity) {
            user.setFireTicks(60);
        }
        
        return result;
    }
}
