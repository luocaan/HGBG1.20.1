package com.hydroceder.hgbg.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * 田野收获者附魔
 * 仅限附魔于腿和鞋
 * 穿戴该附魔的装备时：
 * 每一级都会在玩家处于耕地附近时持续获得1s的缓降效果
 * I级：收获的作物有概率翻倍
 * II级：收获的作物翻倍
 * III级：收获的作物翻倍，且有概率掉落骨粉
 */
public class FieldHarvesterEnchantment extends Enchantment {
    public static FieldHarvesterEnchantment INSTANCE;
    public static final Identifier ID = new Identifier("hunger-begone", "field_harvester");
    
    public FieldHarvesterEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[]{
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
        });
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
        EquipmentSlot slot = armor.getSlotType();
        return slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
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
