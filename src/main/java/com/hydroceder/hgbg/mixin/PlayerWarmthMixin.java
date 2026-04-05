package com.hydroceder.hgbg.mixin;

import com.hydroceder.hgbg.enchantment.WarmthEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 玩家温暖Mixin
 * 当玩家饥饿值大于14，且装备相应附魔的装备，则不会因陷入细雪而冻伤
 */
@Mixin(PlayerEntity.class)
public class PlayerWarmthMixin {
    /**
     * 注入到tick方法，防止细雪伤害
     * 
     * @param ci 回调信息
     */
    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        World world = player.getWorld();
        
        // 只在服务器端执行
        if (world.isClient()) {
            return;
        }
        
        // 检查玩家是否饥饿值大于14
        if (player.getHungerManager().getFoodLevel() > 14) {
            // 检查玩家是否装备有温暖饱腹附魔
            if (hasWarmthEnchantment(player)) {
                // 检查玩家是否站在细雪中
                BlockPos pos = player.getBlockPos();
                if (world.getBlockState(pos).isOf(net.minecraft.block.Blocks.POWDER_SNOW)) {
                    // 防止细雪伤害
                    player.setFrozenTicks(0);
                }
            }
        }
    }
    
    /**
     * 检查玩家是否装备有温暖饱腹附魔
     * 
     * @param player 玩家实体
     * @return 是否装备有温暖饱腹附魔
     */
    private boolean hasWarmthEnchantment(PlayerEntity player) {
        // 遍历玩家的所有装备槽
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getEquippedStack(slot);
            if (EnchantmentHelper.getLevel(WarmthEnchantment.INSTANCE, stack) > 0) {
                return true;
            }
        }
        return false;
    }
}