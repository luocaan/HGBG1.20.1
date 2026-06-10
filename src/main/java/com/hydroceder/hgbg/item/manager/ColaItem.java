package com.hydroceder.hgbg.item.manager;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ColaItem extends DrinkItem {
    public ColaItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 1200, 0));
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 0));
        }

        return result;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // 动态显示汽水带来的药水效果，遵循Minecraft原版效果tooltip格式
        addEffectTooltip(tooltip, StatusEffects.JUMP_BOOST, 0, 1200);
        addEffectTooltip(tooltip, StatusEffects.SPEED, 0, 1200);
    }

    /**
     * 按Minecraft原版格式添加单个效果tooltip
     * 使用StatusEffect的翻译键动态获取效果名称，而非硬编码文本
     */
    protected static void addEffectTooltip(List<Text> tooltip, net.minecraft.entity.effect.StatusEffect effect, int amplifier, int duration) {
        tooltip.add(Text.translatable(
            "item.hunger-begone.effect_tooltip",
            effect.getName(),
            formatDuration(duration)
        ).formatted(Formatting.DARK_PURPLE));
    }

    /**
     * 将tick数格式化为 mm:ss 格式的持续时间文本
     */
    private static String formatDuration(int ticks) {
        int seconds = ticks / 20;
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }
}
