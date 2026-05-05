package com.hydroceder.hgbg.mixin;

import com.hydroceder.hgbg.effect.CalmnessEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class PlayerHealMixin {

    @Inject(at = @At("HEAD"), method = "heal", cancellable = true)
    private void onHeal(float amount, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }

        if (CalmnessEffect.isSettling(player)) {
            return;
        }

        if (CalmnessEffect.hasCalmness(player) && amount > 0) {
            CalmnessEffect.addAccumulatedHeal(player, amount);
            ci.cancel();
        }
    }
}
