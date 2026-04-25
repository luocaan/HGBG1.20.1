package com.hydroceder.hgbg.mixin;

import com.hydroceder.hgbg.util.SeasoningNBT;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 调味料效果Mixin
 * 拦截LivingEntity.eatFood方法，在物品被消耗前检查调味料NBT并给予对应效果
 * 
 * 兼容性设计：
 * - 使用带模组前缀的NBT键（hgbg_seasonings）
 * - 只处理包含本模组NBT的物品，不影响其他模组的物品
 * - 在物品被消耗前注入，确保能正确读取NBT
 * - 支持多个调味料效果
 */
@Mixin(LivingEntity.class)
public class SeasoningEffectMixin {
    @Inject(at = @At("HEAD"), method = "eatFood")
    private void onEatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient) {
            if (SeasoningNBT.hasSeasonings(stack)) {
                LivingEntity entity = (LivingEntity) (Object) this;
                SeasoningNBT.applySeasoningEffects(stack, entity);
            }
        }
    }
}