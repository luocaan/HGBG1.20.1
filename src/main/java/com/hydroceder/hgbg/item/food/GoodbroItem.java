package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * GOODBRO! 物品类
 * 恢复饥饿值20点，饱和度20点
 * 灰色描述："YO------"
 */
public class GoodbroItem extends Item {
    
    public GoodbroItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.GOODBRO_HUNGER,
            FoodProperties.GOODBRO_SATURATION
        ).build()));
    }
    
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.hunger-begone.goodbro.tooltip").formatted(net.minecraft.util.Formatting.GRAY));
    }
}
