package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

/**
 * 巧克力牛奶物品类
 * 饮用后回复饥饿值和饱和度，返回空桶
 */
public class ChocolateMilkItem extends DrinkItem {
    public ChocolateMilkItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.CHOCOLATE_MILK_HUNGER)
            .saturationModifier(FoodProperties.CHOCOLATE_MILK_SATURATION)
            .build()));
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // 先执行默认的食物食用逻辑（恢复饥饿值和饱和度）
        ItemStack result = super.finishUsing(stack, world, user);
        
        // 非创造模式下返回空桶
        if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
            ItemStack bucketStack = new ItemStack(Items.BUCKET);
            
            // 如果食用后结果为空（消耗了最后一个），直接返回空桶
            if (result.isEmpty()) {
                return bucketStack;
            }
            
            // 使用Minecraft标准方法：优先放入背包，否则掉地上
            player.getInventory().offerOrDrop(bucketStack);
        }
        
        return result;
    }
}