package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CrudeCakeItem extends Item {
    public CrudeCakeItem(FabricItemSettings settings) {
        super(settings.maxCount(1).maxDamage(19).food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.CRUDE_CAKE_HUNGER,
            FoodProperties.CRUDE_CAKE_SATURATION
        ).build()));
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, java.util.List<Text> tooltip, net.minecraft.client.item.TooltipContext context) {
        tooltip.add(Text.translatable("item.hunger-begone.crude_cake.tooltip").formatted(Formatting.GRAY));
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            player.getHungerManager().add(FoodProperties.CRUDE_CAKE_HUNGER, FoodProperties.CRUDE_CAKE_SATURATION);
            
            if (stack.getDamage() < 19) {
                stack.setDamage(stack.getDamage() + 1);
            } else {
                stack.decrement(1);
            }
        }
        
        return stack;
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 16;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }
}
