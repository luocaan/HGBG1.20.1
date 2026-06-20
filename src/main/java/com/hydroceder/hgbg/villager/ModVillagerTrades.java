package com.hydroceder.hgbg.villager;

import com.hydroceder.hgbg.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

public class ModVillagerTrades {

    public static void register() {
        // Farmer level 1 (novice) — cheap seed & bean trades
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1, factories -> {
            // 1 emerald → 4 eggplant seeds
            factories.add((entity, random) -> new TradeOffer(
                new ItemStack(Items.EMERALD, 1),
                ItemStack.EMPTY,
                new ItemStack(ModItems.EGGPLANT_SEED, 4),
                0,      // uses
                16,     // maxUses before lock
                2,      // experience granted
                0.05f,  // price multiplier
                0       // demand bonus
            ));

            // 1 emerald → 4 soybeans
            factories.add((entity, random) -> new TradeOffer(
                new ItemStack(Items.EMERALD, 1),
                ItemStack.EMPTY,
                new ItemStack(ModItems.SOYBEAN, 4),
                0, 16, 2, 0.05f, 0
            ));
        });
    }
}
