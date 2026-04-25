package com.hydroceder.hgbg.mixin;

import com.hydroceder.hgbg.seasoning.Seasoning;
import com.hydroceder.hgbg.util.SeasoningNBT;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * 调味料提示Mixin
 * 为带有调味料NBT的物品添加tooltip显示
 * 
 * 支持所有物品（包括原版和其它模组）
 * 支持多个调味料
 */
@Mixin(Item.class)
public class SeasoningTooltipMixin {
    @Inject(at = @At("TAIL"), method = "appendTooltip")
    private void onAppendTooltip(ItemStack stack, net.minecraft.world.World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (SeasoningNBT.hasSeasonings(stack)) {
            for (Seasoning seasoning : SeasoningNBT.getSeasonings(stack)) {
                tooltip.add(Text.translatable(seasoning.getTranslationKey()).formatted(Formatting.GRAY));
            }
        }
    }
}