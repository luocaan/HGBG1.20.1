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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * 锅武器类
 */
public class PanItem extends SwordItem {
    private static final int ADDITIONAL_ATTACK_DAMAGE = 13;
    
    private static final float ATTACK_SPEED = -2.5f;
    
    // NBT 键名
    private static final String COOKING_COUNT_KEY = "CookingCount";
    
    public PanItem(ToolMaterial material, Settings settings) {
        super(material, ADDITIONAL_ATTACK_DAMAGE, ATTACK_SPEED, settings);
    }
    
    /**
     * 获取锅的烹饪次数
     */
    public static int getCookingCount(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(COOKING_COUNT_KEY)) {
            return nbt.getInt(COOKING_COUNT_KEY);
        }
        return 0;
    }
    
    /**
     * 增加锅的烹饪次数
     */
    public static void incrementCookingCount(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        int count = nbt.getInt(COOKING_COUNT_KEY);
        nbt.putInt(COOKING_COUNT_KEY, count + 1);
    }
    
    /**
     * 添加物品提示信息
     */
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        
        // 添加灰色描述文本
        tooltip.add(Text.translatable("item.hunger-begone.pan.tooltip").formatted(Formatting.GRAY));
        
        int cookingCount = getCookingCount(stack);
        if (cookingCount > 0) {
            tooltip.add(Text.translatable("item.hunger-begone.pan.cooking_count", cookingCount));
        }
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
        
        // 在攻击者身上播放敲击音效（音效系统自动随机选择hit/hit2）
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
            
            // 检测是否有"热情高涨"附魔
            if (EnchantmentHelper.getLevel(EnthusiasmEnchantment.INSTANCE, stack) > 0) {
                // 计算玩家饱和度
                float saturation = player.getHungerManager().getSaturationLevel();
                
                // 计算药水持续时间（最大28秒）
                int duration = (int) (saturation * 1.3 * 20); // 转换为tick
                if (duration > 28 * 20) {
                    duration = 28 * 20;
                }
                
                // 取消击退效果
                // Vec3d knockbackDirection = player.getRotationVector().normalize().multiply(knockbackDistance);
                // target.addVelocity(knockbackDirection.x, 0.5, knockbackDirection.z);
                // target.velocityModified = true;
                
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