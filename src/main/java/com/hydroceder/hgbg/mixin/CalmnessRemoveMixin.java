package com.hydroceder.hgbg.mixin;

import com.hydroceder.hgbg.effect.CalmnessEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class CalmnessRemoveMixin {

    @Inject(at = @At("HEAD"), method = "onStatusEffectRemoved", cancellable = false)
    private void onEffectRemoved(StatusEffectInstance effect, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }

        if (effect.getEffectType() == CalmnessEffect.INSTANCE) {
            // 如果是刷新效果导致的移除，不结算
            if (CalmnessEffect.isRefreshing(player)) {
                return;
            }
            CalmnessEffect.settleCalmness(player);
        }
    }
}
