package com.hydroceder.hgbg.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import com.hydroceder.hgbg.block.ModBlocks;
import com.hydroceder.hgbg.enchantment.NourishmentEnchantment;
import com.hydroceder.hgbg.enchantment.NutritionEnchantment;
import com.hydroceder.hgbg.enchantment.SpeedEnchantment;
import com.hydroceder.hgbg.enchantment.WarmthEnchantment;
import com.hydroceder.hgbg.enchantment.EnthusiasmEnchantment;
import com.hydroceder.hgbg.enchantment.FieldHarvesterEnchantment;

/**
 * 模组物品组注册类
 * 用于在创造模式中显示模组物品
 */
public class ModItemGroups {
    public static ItemGroup HUNGER_BEGONE_GROUP;
    
    public static void register() {
        // 创建物品组
        HUNGER_BEGONE_GROUP = FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.PAN))
                .displayName(Text.translatable("itemGroup.hunger-begone"))
                .entries((displayContext, entries) -> {
                    // 添加锅武器到物品组
                    entries.add(ModItems.PAN);
                    
                    // 添加灶台到物品组
                    entries.add(ModBlocks.STOVE);
                    
                    // 添加烤箱到物品组
                    entries.add(ModBlocks.OVEN);
                    
                    // 添加研钵与杵到物品组
                    entries.add(ModBlocks.MORTAR_AND_PESTLE);
                    
                    // 添加置物架到物品组
                    entries.add(ModBlocks.SHELF);

                    // 添加锅铲到物品组
                    entries.add(ModItems.SPATULA);
                    
                    // 添加法棍面包到物品组
                    entries.add(ModItems.BAGUETTE);
                    
                    // 添加生培根和培根到物品组
                    entries.add(ModItems.RAW_BACON);
                    entries.add(ModItems.BACON);
                    
                    // 添加曲奇到物品组
                    entries.add(ModItems.COPPER_COOKIE);
                    entries.add(ModItems.IRON_COOKIE);
                    entries.add(ModItems.DIAMOND_COOKIE);
                    
                    // 添加奶油蘑菇汤到物品组
                    entries.add(ModItems.CREAMY_MUSHROOM_SOUP);
                    
                    // 添加柠檬泡菜到物品组
                    entries.add(ModItems.LEMON_PICKLE);
                    
                    // 添加奥尔良烤鸡到物品组
                    entries.add(ModItems.ORLEANS_ROASTED_CHICKEN);
                    
                    // 添加柠檬鸡爪到物品组
                    entries.add(ModItems.LEMON_CHICKEN_FEET);
                    
                    // 添加果酱面包到物品组
                    entries.add(ModItems.JAM_BREAD);
                    
                    // 添加果酱系列与一些纹理相似饮料到物品组
                    entries.add(ModItems.SHINY_BERRY_JAM_BREAD);
                    entries.add(ModItems.APPLE_JAM_BREAD);
                    entries.add(ModItems.BERRY_JAM_BREAD);
                    entries.add(ModItems.WATERMELON_JAM_BREAD);
                    entries.add(ModItems.CHOCOLATE_BREAD);
                    entries.add(ModItems.ROSE_JAM_BREAD);
                    entries.add(ModItems.SHINY_BERRY_JAM_BOTTLE);
                    entries.add(ModItems.MUSHROOM_OIL_BOTTLE);
                    entries.add(ModItems.MUSHROOM_JAM_BOTTLE);
                    entries.add(ModItems.APPLE_JAM_BOTTLE);
                    entries.add(ModItems.BERRY_JAM_BOTTLE);
                    entries.add(ModItems.ROSE_JAM_BOTTLE);
                    entries.add(ModItems.WATERMELON_JAM_BOTTLE);
                    entries.add(ModItems.DAISY_FLOWER_TEA);
                    entries.add(ModItems.FLOWER_TEA);
                    entries.add(ModItems.HOT_CHOCOLATE_BOTTLE);
                    entries.add(ModItems.MAYONNAISE_BOTTLE);
                    
                    // 添加柠檬水到物品组
                    entries.add(ModItems.LEMON_WATER);
                    
                    // 添加全糖柠檬水到物品组
                    entries.add(ModItems.FULL_SUGAR_LEMONADE);
                    
                    // 添加新果酱和饮料到物品组
                    entries.add(ModItems.SHINY_BERRY_COLA_BOTTLE);
                    entries.add(ModItems.BERRY_COLA_BOTTLE);
                    entries.add(ModItems.APPLE_COLA_BOTTLE);
                    entries.add(ModItems.LEMON_COLA_BOTTLE);
                    
                    // 添加巧克力牛奶到物品组
                    entries.add(ModItems.CHOCOLATE_MILK);

                    // 添加生虾、熟虾和GOODBRO!到物品组
                    entries.add(ModItems.RAW_SHRIMP);
                    entries.add(ModItems.COOKED_SHRIMP);
                    entries.add(ModItems.GOODBRO);
                    
                    // 添加薯条和炸鱼薯条到物品组
                    entries.add(ModItems.CHIPS);
                    entries.add(ModItems.FISH_AND_CHIPS);

                    // 添加柠檬到物品组
                    entries.add(ModItems.LEMON);
                    
                    // 添加苔藓到物品组
                    entries.add(ModItems.MOSS);
                    
                    // 添加粗制蛋糕到物品组
                    entries.add(ModItems.CRUDE_CAKE);
                    
                    // 添加The_Newage唱片到物品组
                    entries.add(ModItems.MUSIC_DISC_THE_NEWAGE);
                    
                    // 添加Afternoon唱片到物品组
                    entries.add(ModItems.MUSIC_DISC_AFTERNOON);
                    
                    // 添加故乡土壤到物品组
                    entries.add(ModItems.HOMELAND_DIRT);
                    
                    // 添加本模组的附魔书到物品组
                    entries.add(EnchantedBookItem.forEnchantment(new net.minecraft.enchantment.EnchantmentLevelEntry(NourishmentEnchantment.INSTANCE, 1)));
                    entries.add(EnchantedBookItem.forEnchantment(new net.minecraft.enchantment.EnchantmentLevelEntry(NutritionEnchantment.INSTANCE, 1)));
                    entries.add(EnchantedBookItem.forEnchantment(new net.minecraft.enchantment.EnchantmentLevelEntry(SpeedEnchantment.INSTANCE, 1)));
                    entries.add(EnchantedBookItem.forEnchantment(new net.minecraft.enchantment.EnchantmentLevelEntry(WarmthEnchantment.INSTANCE, 1)));
                    entries.add(EnchantedBookItem.forEnchantment(new net.minecraft.enchantment.EnchantmentLevelEntry(EnthusiasmEnchantment.INSTANCE, 1)));
                    entries.add(EnchantedBookItem.forEnchantment(new net.minecraft.enchantment.EnchantmentLevelEntry(FieldHarvesterEnchantment.INSTANCE, 1)));
                    entries.add(EnchantedBookItem.forEnchantment(new net.minecraft.enchantment.EnchantmentLevelEntry(FieldHarvesterEnchantment.INSTANCE, 2)));
                    entries.add(EnchantedBookItem.forEnchantment(new net.minecraft.enchantment.EnchantmentLevelEntry(FieldHarvesterEnchantment.INSTANCE, 3)));
                })
                .build();
        
        // 注册物品组
        Registry.register(Registries.ITEM_GROUP, new Identifier("hunger-begone", "items"), HUNGER_BEGONE_GROUP);
    }
}