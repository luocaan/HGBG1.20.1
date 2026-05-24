package com.hydroceder.hgbg.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class EnhancedInsecticideEnchantment extends Enchantment {
    public static EnhancedInsecticideEnchantment INSTANCE;
    public static final Identifier ID = new Identifier("hunger-begone", "enhanced_insecticide");
    private static final TagKey<Item> ENCHANTABLE_TAG = TagKey.of(RegistryKeys.ITEM, new Identifier("hunger-begone", "enchantable/insecticide"));

    public EnhancedInsecticideEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinPower(int level) {
        return 12;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 25;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isIn(ENCHANTABLE_TAG);
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
