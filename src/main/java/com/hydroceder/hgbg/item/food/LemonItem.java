package com.hydroceder.hgbg.item.food;

import com.hydroceder.hgbg.item.manager.FoodProperties;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LemonItem extends Item {
    public LemonItem(FabricItemSettings settings) {
        super(settings.food(FoodProperties.createAlwaysEdibleFood(
            FoodProperties.LEMON_HUNGER,
            FoodProperties.LEMON_SATURATION
        ).build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 160, 0, false, false));
        }

        return result;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.hunger-begone.lemon.tooltip").formatted(Formatting.GRAY));
    }
}
