package com.hydroceder.hgbg.enchantment;

import com.hydroceder.hgbg.item.tool.PanItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * 热情高涨附魔
 * 只能附魔到锅武器上
 * 当玩家使用有该附魔的锅命中目标时，会计算玩家移动速度，并击退目标，赋予目标迟滞效果
 */
public class EnthusiasmEnchantment extends Enchantment {
    public static EnthusiasmEnchantment INSTANCE;
    public static final Identifier ID = new Identifier("hunger-begone", "enthusiasm_enchantment");
    
    public EnthusiasmEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.values());
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
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof PanItem;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return true;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }
}