package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * 果酱面包物品类
 * 恢复6饥饿值，7饱和度
 */
public class JamBreadItem extends Item {
    public JamBreadItem(Settings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.JAM_BREAD_HUNGER,
            FoodProperties.JAM_BREAD_SATURATION
        ).build()));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.hunger-begone.jam_bread.tooltip").formatted(Formatting.GRAY));
    }
}
