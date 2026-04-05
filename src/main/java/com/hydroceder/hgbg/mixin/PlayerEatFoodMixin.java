package com.hydroceder.hgbg.mixin;

import com.hydroceder.hgbg.enchantment.NutritionEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 玩家进食Mixin
 * 在玩家完成食用食物后增加经验等级
 */
@Mixin(PlayerEntity.class)
public class PlayerEatFoodMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("hunger-begone");
     
    /**
     * 注入到eatFood方法末尾，在玩家完成食用食物后增加经验等级
     * 
     * @param ci 回调信息
     */
    @Inject(at = @At("TAIL"), method = "eatFood")
    private void onEatFood(CallbackInfoReturnable<ItemStack> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // 只在服务器端执行，避免客户端重复增加经验
        if (player.getWorld().isClient()) {
            return;
        }
        
        // 获取玩家装备的营养学附魔等级
        int nutritionLevel = getNutritionEnchantmentLevel(player);
        
        if (nutritionLevel > 0) {
            // 为玩家增加经验等级
            player.addExperienceLevels(nutritionLevel);
        }
    }
    
    /**
     * 获取玩家装备的营养学附魔等级
     * 
     * @param player 玩家实体
     * @return 营养学附魔的最高等级，如果没有则返回0
     */
    private int getNutritionEnchantmentLevel(PlayerEntity player) {
        int maxLevel = 0;
        // 遍历玩家的所有装备槽
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getEquippedStack(slot);
            int level = EnchantmentHelper.getLevel(NutritionEnchantment.INSTANCE, stack);
            if (level > maxLevel) {
                maxLevel = level;
            }
        }
        return maxLevel;
    }
}