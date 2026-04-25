package com.hydroceder.hgbg.item.tool;

import com.hydroceder.hgbg.enchantment.EnthusiasmEnchantment;
import com.hydroceder.hgbg.effect.SlownessPotionEffect;
import com.hydroceder.hgbg.sound.ModSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * 冲锋锅武器类
 * 与普通锅类似，但不可参与烹饪
 */
public class PotChargeItem extends SwordItem {
    private static final int ADDITIONAL_ATTACK_DAMAGE = 20;
    
    private static final float ATTACK_SPEED = 0.5f;
    
    public PotChargeItem(ToolMaterial material, Settings settings) {
        super(material, ADDITIONAL_ATTACK_DAMAGE, ATTACK_SPEED, settings);
    }
    
    /**
     * 添加物品提示信息
     */
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        
        // 添加灰色描述文本
        tooltip.add(Text.translatable("item.hunger-begone.pot_charge.tooltip").formatted(Formatting.GRAY));
    }
    
    /**
     * 重写此方法在攻击命中时播放敲击音效
     * 
     * @param target 被攻击的目标实体
     * @param attacker 攻击者实体
     */
    @Override
    public boolean postHit(net.minecraft.item.ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 调用父类方法处理耐久度等
        boolean result = super.postHit(stack, target, attacker);
        
        // 在攻击者身上播放敲击音效
        World world = attacker.getWorld();
        if (!world.isClient && attacker instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) attacker;
            // 使用更高的音量播放音效
            world.playSound(
                null, // null表示播放给所有玩家
                player.getX(), 
                player.getY(), 
                player.getZ(),
                ModSounds.HIT,
                SoundCategory.PLAYERS,
                0.8f, // 音量
                1.0f  // 音调
            );
            
            // 检测是否有“热情高涨”附魔
            if (EnchantmentHelper.getLevel(EnthusiasmEnchantment.INSTANCE, stack) > 0) {
                // 计算玩家饱和度
                float saturation = player.getHungerManager().getSaturationLevel();
                
                // 计算药水持续时间（最大28秒）
                int duration = (int) (saturation * 1.3 * 20); // 转换为tick
                if (duration > 28 * 20) {
                    duration = 28 * 20;
                }
                
                // 赋予目标迟滞效果
                target.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(SlownessPotionEffect.INSTANCE, duration, 0));
                
                // 检测玩家是否着火，如果着火则伤害翻倍
                if (player.isOnFire()) {
                    // 对目标造成额外伤害，相当于伤害翻倍
                    target.damage(player.getDamageSources().playerAttack(player), 30.0f);
                }
            }
        }
        
        return result;
    }
}