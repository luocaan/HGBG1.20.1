package com.hydroceder.hgbg.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;

/**
 * 滋养附魔类
 * 当玩家持有具有该附魔的武器/工具时，杀死实体后会获得与实体最大生命值等量的饱和度
 */
public class NourishmentEnchantment extends Enchantment {
    /**
     * 附魔的唯一标识符
     */
    public static final Identifier ID = new Identifier("hunger-begone", "nourishment");
    /**
     * 附魔实例
     */
    public static NourishmentEnchantment INSTANCE;

    /**
     * 构造函数
     * 设置附魔的稀有度为UNCOMMON，目标为装备，可应用到所有装备槽
     */
    public NourishmentEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof SwordItem ||
               stack.getItem() instanceof AxeItem ||
               stack.getItem() instanceof PickaxeItem ||
               stack.getItem() instanceof ShovelItem ||
               stack.getItem() instanceof HoeItem ||
               stack.getItem() instanceof ToolItem ||
               stack.getItem() instanceof ArmorItem;
    }

    /**
     * 获取附魔的最大等级
     * @return 最大等级，固定为2
     */
    @Override
    public int getMaxLevel() {
        return 2;
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