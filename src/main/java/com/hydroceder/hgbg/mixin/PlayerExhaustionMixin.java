package com.hydroceder.hgbg.mixin;

import com.hydroceder.hgbg.enchantment.SpeedEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 玩家疲劳度拦截Mixin
 * 当玩家疾跑且穿戴饱食疾行附魔时，阻止疲劳度的增加
 */
@Mixin(PlayerEntity.class)
public class PlayerExhaustionMixin {
    /**
     * 拦截addExhaustion方法的调用
     * 当玩家疾跑且穿戴饱食疾行附魔时，取消疲劳度的增加
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
        
        // 检查玩家是否正在疾跑且穿戴饱食疾行附魔
        if (player.isSprinting() && hasSpeedEnchantment(player)) {
            // 取消疲劳度的增加
            ci.cancel();
        }
    }
    
    /**
     * 检查玩家是否穿戴了饱食疾行附魔的装备
     * 
     * @param player 玩家实体
     * @return 是否穿戴了饱食疾行附魔
     */
    private boolean hasSpeedEnchantment(PlayerEntity player) {
        // 检查脚部装备
        ItemStack feetItem = player.getEquippedStack(EquipmentSlot.FEET);
        return EnchantmentHelper.getLevel(SpeedEnchantment.INSTANCE, feetItem) > 0;
    }
}