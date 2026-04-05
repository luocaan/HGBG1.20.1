package com.hydroceder.hgbg.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 玩家疲劳度拦截Mixin
 * 当玩家疾跑时，阻止疲劳度的增加
 */
@Mixin(PlayerEntity.class)
public class PlayerExhaustionMixin {
    /**
     * 拦截addExhaustion方法的调用
     * 当玩家疾跑时，取消疲劳度的增加
     * 
     * @param exhaustion 要增加的疲劳度值
     * @param ci 回调信息
     */
    @Inject(at = @At("HEAD"), method = "addExhaustion", cancellable = true)
    private void onAddExhaustion(float exhaustion, CallbackInfo ci) {
        // 只在服务器端执行，避免客户端重复处理
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient()) {
            return;
        }
        
        // 检查配置是否开启疾跑不增加疲劳的功能
        if (!com.hydroceder.hgbg.config.ModConfig.isSprintNoExhaustionEnabled()) {
            return;
        }
        
        // 检查玩家是否正在疾跑
        if (player.isSprinting()) {
            // 取消疲劳度的增加
            ci.cancel();
        }
    }
}