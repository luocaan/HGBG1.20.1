package com.hydroceder.hgbg.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * 营养学附魔类
 * 当玩家穿戴具有该附魔的装备时，每次进食会增加经验值
 * I级：增加1级经验
 * II级：增加2级经验
 * III级：增加3级经验
 */
public class NutritionEnchantment extends Enchantment {
    /**
     * 附魔的唯一标识符
     */
    public static final Identifier ID = new Identifier("hunger-begone", "nutrition");
    /**
     * 附魔实例
     */
    public static NutritionEnchantment INSTANCE;

    /**
     * 构造函数
     * 设置附魔的稀有度为UNCOMMON，目标为可穿戴装备，可应用到所有装备槽
     */
    public NutritionEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.ARMOR, EquipmentSlot.values());
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    /**
     * 获取附魔的最大等级
     * @return 最大等级，固定为3
     */
    @Override
    public int getMaxLevel() {
        return 3;
    }

    /**
     * 检查是否为宝藏附魔
     * @return 总是返回false，不是宝藏附魔
     */
    @Override
    public boolean isTreasure() {
        return false;
    }

    /**
     * 检查是否可在附魔书中出现
     * @return 总是返回true，可以在附魔书中出现
     */
    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return true;
    }

    /**
     * 检查是否可在随机选择中出现
     * @return 总是返回true，可以在随机选择中出现
     */
    @Override
    public boolean isAvailableForRandomSelection() {
        return true;
    }
}