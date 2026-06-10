package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.ColaItem;
import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShinyBerryColaBottleItem extends ColaItem {
    public ShinyBerryColaBottleItem(Settings settings) {
        super(settings.food(createFoodComponent()
            .hunger(FoodProperties.SHINY_BERRY_COLA_HUNGER)
            .saturationModifier(FoodProperties.SHINY_BERRY_COLA_SATURATION)
            .build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        // 饮用后获得1200ticks（60秒）的发光效果
        if (!world.isClient && user instanceof PlayerEntity) {
            user.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.GLOWING,
                    1200,
                    0,
                    false,
                    true
            ));
        }

        return result;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // 先显示基础汽水效果（跳跃提升 + 速度）
        super.appendTooltip(stack, world, tooltip, context);
        // 再显示额外的发光效果
        addEffectTooltip(tooltip, StatusEffects.GLOWING, 0, 1200);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
