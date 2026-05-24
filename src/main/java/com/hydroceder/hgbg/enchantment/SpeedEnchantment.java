package com.hydroceder.hgbg.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * 饱食疾行附魔
 * 仅能附魔于鞋，当玩家饱和度＞0时额外增加疾跑速度
 * 共3级，I级增加20%，II级增加30%，III级增加40%
 */
public class SpeedEnchantment extends Enchantment {
    public static SpeedEnchantment INSTANCE;
    public static final Identifier ID = new Identifier("hunger-begone", "speed_enchantment");
    
    public SpeedEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }
    
    @Override
    public int getMaxLevel() {
        return 3;
    }
    
    @Override
    public int getMinPower(int level) {
        return 10 + (level - 1) * 10;
    }
    
    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 30;
    }
    
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem)) {
            return false;
        }
        ArmorItem armor = (ArmorItem) stack.getItem();
        return armor.getSlotType() == EquipmentSlot.FEET;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return true;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return true;
    }
}