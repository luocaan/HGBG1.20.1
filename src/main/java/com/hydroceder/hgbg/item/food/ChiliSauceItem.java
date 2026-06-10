package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.DrinkItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChiliSauceItem extends DrinkItem {
    private static final int FIRE_TICKS = 60;

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
            user.setFireTicks(FIRE_TICKS);
        }

        return result;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // 动态显示辣椒酱的着火效果（非StatusEffect，使用独立描述）
        tooltip.add(Text.translatable(
            "item.hunger-begone.fire_effect_tooltip",
            FIRE_TICKS / 20
        ).formatted(Formatting.RED));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
