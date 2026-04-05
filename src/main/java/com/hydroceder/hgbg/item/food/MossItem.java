package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MossItem extends Item {
    public MossItem(FabricItemSettings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.MOSS_HUNGER,
            FoodProperties.MOSS_SATURATION
        ).build()));
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);
        
        if (!world.isClient && user instanceof PlayerEntity) {
            user.heal(3.0f);
        }
        
        return result;
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 8;
    }
}
