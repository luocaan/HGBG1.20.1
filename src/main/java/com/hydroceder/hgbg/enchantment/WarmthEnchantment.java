package com.hydroceder.hgbg.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * 温暖饱腹附魔
 * 当玩家饥饿值大于14，且装备相应附魔的装备，则不会因陷入细雪而冻伤
 * 仅适用于可穿戴装备
 */
public class WarmthEnchantment extends Enchantment {
    public static WarmthEnchantment INSTANCE;
    public static final Identifier ID = new Identifier("hunger-begone", "warmth_enchantment");
    
    public WarmthEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR, EquipmentSlot.values());
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }
    
    @Override
    public int getMaxLevel() {
        return 1;
    }
    
    @Override
    public int getMinPower(int level) {
        return 10;
    }
    
    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 30;
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