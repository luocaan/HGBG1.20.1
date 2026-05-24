package com.hydroceder.hgbg.mixin;

import com.hydroceder.hgbg.item.tool.InsecticideItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class InsecticideEnchantmentMixin {

    @Inject(at = @At("HEAD"), method = "getPossibleEntries", cancellable = true)
    private static void replaceEntries(int power, ItemStack stack, boolean treasureAllowed,
                                        CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        if (!(stack.getItem() instanceof InsecticideItem)) return;

        List<EnchantmentLevelEntry> list = new ArrayList<>();
        boolean isBook = stack.isOf(Items.BOOK);

        for (Enchantment enchantment : Registries.ENCHANTMENT) {
            if (enchantment.isTreasure() && !treasureAllowed) continue;
            if (!enchantment.isAvailableForRandomSelection() && !isBook) continue;

            for (int i = enchantment.getMaxLevel(); i >= enchantment.getMinLevel(); i--) {
                if (power < enchantment.getMinPower(i) || power > enchantment.getMaxPower(i)) continue;

                boolean acceptable = isBook
                    || enchantment == Enchantments.FIRE_ASPECT
                    || enchantment == Enchantments.BANE_OF_ARTHROPODS
                    || enchantment == Enchantments.SMITE
                    || enchantment.isAcceptableItem(stack);

                if (!acceptable) continue;

                list.add(new EnchantmentLevelEntry(enchantment, i));
                break;
            }
        }

        cir.setReturnValue(list);
        cir.cancel();
    }
}