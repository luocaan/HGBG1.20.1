package com.hydroceder.hgbg.item;

import com.hydroceder.hgbg.item.food.*;
import com.hydroceder.hgbg.item.tool.*;
import com.hydroceder.hgbg.item.material.*;
import com.hydroceder.hgbg.sound.ModSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModItems {
    private static final Logger LOGGER = LoggerFactory.getLogger("hunger-begone");
    private static final String MOD_ID = "hunger-begone";

    private static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    // 锅武器实例
    public static final Item PAN = Registry.register(
        Registries.ITEM,
        id("pan"),
        new PanItem(PanMaterial.INSTANCE, new FabricItemSettings())
    );

    // 冲锋锅武器实例
    public static final Item POT_CHARGE = Registry.register(
        Registries.ITEM,
        id("pot_charge"),
        new PotChargeItem(PanMaterial.INSTANCE, new FabricItemSettings())
    );

    // 生培根实例
    public static final Item RAW_BACON = Registry.register(
        Registries.ITEM,
        id("raw_bacon"),
        new RawBaconItem(new FabricItemSettings())
    );

    // 培根实例
    public static final Item BACON = Registry.register(
        Registries.ITEM,
        id("bacon"),
        new BaconItem(new FabricItemSettings())
    );

    // 涂蜡的镶铜曲奇实例
    public static final Item COPPER_COOKIE = Registry.register(
        Registries.ITEM,
        id("copper_cookie"),
        new CopperCookieItem(new FabricItemSettings())
    );

    // 镶铁曲奇实例
    public static final Item IRON_COOKIE = Registry.register(
        Registries.ITEM,
        id("iron_cookie"),
        new IronCookieItem(new FabricItemSettings())
    );

    // 镶钻曲奇实例
    public static final Item DIAMOND_COOKIE = Registry.register(
        Registries.ITEM,
        id("diamond_cookie"),
        new DiamondCookieItem(new FabricItemSettings())
    );

    // 奶油蘑菇汤实例
    public static final Item CREAMY_MUSHROOM_SOUP = Registry.register(
        Registries.ITEM,
        id("creamy_mushroom_soup"),
        new CreamyMushroomSoupItem(new FabricItemSettings().maxCount(1))
    );

    // 果酱面包实例
    public static final Item JAM_BREAD = Registry.register(
        Registries.ITEM,
        id("jam_bread"),
        new JamBreadItem(new FabricItemSettings())
    );

    // 发光果酱瓶实例
    public static final Item SHINY_BERRY_JAM_BOTTLE = Registry.register(
        Registries.ITEM,
        id("shiny_berry_jam_bottle"),
        new ShinyBerryJamBottleItem(new FabricItemSettings().maxCount(24))
    );

    // 发光果酱面包实例
    public static final Item SHINY_BERRY_JAM_BREAD = Registry.register(
        Registries.ITEM,
        id("shiny_berry_jam_bread"),
        new ShinyBerryJamBreadItem(new FabricItemSettings())
    );

    // 菌油瓶实例
    public static final Item MUSHROOM_OIL_BOTTLE = Registry.register(
        Registries.ITEM,
        id("mushroom_oil_bottle"),
        new MushroomOilItem(new FabricItemSettings())
    );

    // 蘑菇酱瓶实例
    public static final Item MUSHROOM_JAM_BOTTLE = Registry.register(
        Registries.ITEM,
        id("mushroom_jam_bottle"),
        new JamItem(new FabricItemSettings().maxCount(24))
    );

    // 苹果酱瓶实例
    public static final Item APPLE_JAM_BOTTLE = Registry.register(
        Registries.ITEM,
        id("apple_jam_bottle"),
        new JamItem(new FabricItemSettings().maxCount(24))
    );

    // 苹果酱面包实例
    public static final Item APPLE_JAM_BREAD = Registry.register(
        Registries.ITEM,
        id("apple_jam_bread"),
        new JamBreadFoodItem(new FabricItemSettings())
    );

    // 巧克力牛奶实例
    public static final Item CHOCOLATE_MILK = Registry.register(
        Registries.ITEM,
        id("chocolate_milk"),
        new ChocolateMilkItem(new FabricItemSettings().maxCount(24))
    );

    // 浆果果酱瓶实例
    public static final Item BERRY_JAM_BOTTLE = Registry.register(
        Registries.ITEM,
        id("berry_jam_bottle"),
        new JamItem(new FabricItemSettings().maxCount(24))
    );

    // 浆果果酱面包实例
    public static final Item BERRY_JAM_BREAD = Registry.register(
        Registries.ITEM,
        id("berry_jam_bread"),
        new JamBreadFoodItem(new FabricItemSettings())
    );

    // 玫瑰果酱瓶实例
    public static final Item ROSE_JAM_BOTTLE = Registry.register(
        Registries.ITEM,
        id("rose_jam_bottle"),
        new com.hydroceder.hgbg.item.food.RoseJamBottleItem(new FabricItemSettings().maxCount(24))
    );

    // 烤玫瑰酱面包实例
    public static final Item ROSE_JAM_BREAD = Registry.register(
        Registries.ITEM,
        id("rose_jam_bread"),
        new com.hydroceder.hgbg.item.food.RoseJamBreadItem(new FabricItemSettings())
    );

    // 西瓜汁瓶实例
    public static final Item WATERMELON_JAM_BOTTLE = Registry.register(
        Registries.ITEM,
        id("watermelon_jam_bottle"),
        new JamItem(new FabricItemSettings().maxCount(24))
    );

    // 西瓜汁面包实例
    public static final Item WATERMELON_JAM_BREAD = Registry.register(
        Registries.ITEM,
        id("watermelon_jam_bread"),
        new JamBreadFoodItem(new FabricItemSettings())
    );

    // 热可可实例
    public static final Item HOT_CHOCOLATE_BOTTLE = Registry.register(
        Registries.ITEM,
        id("hot_chocolate_bottle"),
        new HotChocolateBottleItem(new FabricItemSettings().maxCount(24))
    );

    // 巧克力酱面包实例
    public static final Item CHOCOLATE_BREAD = Registry.register(
        Registries.ITEM,
        id("chocolate_bread"),
        new JamBreadFoodItem(new FabricItemSettings())
    );

    // 蛋黄酱瓶实例
    public static final Item MAYONNAISE_BOTTLE = Registry.register(
        Registries.ITEM,
        id("mayonnaise_bottle"),
        new MayonnaiseBottleItem(new FabricItemSettings())
    );

    // 锅铲实例
    public static final Item SPATULA = Registry.register(
        Registries.ITEM,
        id("spatula"),
        new SpatulaItem(PanMaterial.INSTANCE, new FabricItemSettings())
    );

    // 法棍面包实例
    public static final Item BAGUETTE = Registry.register(
        Registries.ITEM,
        id("baguette"),
        new BaguetteItem(PanMaterial.INSTANCE, new FabricItemSettings())
    );

    // 生虾实例
    public static final Item RAW_SHRIMP = Registry.register(
        Registries.ITEM,
        id("raw_shrimp"),
        new RawShrimpItem(new FabricItemSettings())
    );

    // 熟虾实例
    public static final Item COOKED_SHRIMP = Registry.register(
        Registries.ITEM,
        id("cooked_shrimp"),
        new CookedShrimpItem(new FabricItemSettings())
    );

    // GOODBRO! 实例
    public static final Item GOODBRO = Registry.register(
        Registries.ITEM,
        id("goodbro"),
        new GoodbroItem(new FabricItemSettings())
    );

    // The_Newage 唱片实例
    public static final Item MUSIC_DISC_THE_NEWAGE = Registry.register(
        Registries.ITEM,
        id("music_disc_the_newage"),
        new ModMusicDiscItem(
            15,
            ModSounds.MUSIC_DISC_THE_NEWAGE,
            new FabricItemSettings().maxCount(1).rarity(net.minecraft.util.Rarity.RARE),
            162
        )
    );
    
    // Afternoon 唱片实例
    public static final Item MUSIC_DISC_AFTERNOON = Registry.register(
        Registries.ITEM,
        id("music_disc_afternoon"),
        new ModMusicDiscItem(
            14,
            ModSounds.MUSIC_DISC_AFTERNOON,
            new FabricItemSettings().maxCount(1).rarity(net.minecraft.util.Rarity.RARE),
            150
        )
    );
    
    // 故乡土壤实例
    public static final Item HOMELAND_DIRT = Registry.register(
        Registries.ITEM,
        id("homeland_dirt"),
        new HomelandDirtItem(new FabricItemSettings().maxCount(1))
    );

    // 发光果酱可乐实例
    public static final Item SHINY_BERRY_COLA_BOTTLE = Registry.register(
        Registries.ITEM,
        id("shiny_berry_cola_bottle"),
        new ShinyBerryColaBottleItem(new FabricItemSettings().maxCount(24))
    );

    // 莓可乐实例
    public static final Item BERRY_COLA_BOTTLE = Registry.register(
        Registries.ITEM,
        id("berry_cola_bottle"),
        new BerryColaBottleItem(new FabricItemSettings().maxCount(24))
    );

    // 苹果可乐实例
    public static final Item APPLE_COLA_BOTTLE = Registry.register(
        Registries.ITEM,
        id("apple_cola_bottle"),
        new AppleColaBottleItem(new FabricItemSettings().maxCount(24))
    );
    
    // 柠檬汽水实例
    public static final Item LEMON_COLA_BOTTLE = Registry.register(
        Registries.ITEM,
        id("lemon_cola_bottle"),
        new com.hydroceder.hgbg.item.food.LemonColaBottleItem(new FabricItemSettings().maxCount(24))
    );

    // 菊花茶实例
    public static final Item DAISY_FLOWER_TEA = Registry.register(
        Registries.ITEM,
        id("daisy_flower_tea"),
        new DaisyFlowerTeaItem(new FabricItemSettings().maxCount(24))
    );
    
    // 蜜浆花茶实例
    public static final Item FLOWER_TEA = Registry.register(
        Registries.ITEM,
        id("flower_tea"),
        new FlowerTeaItem(new FabricItemSettings().maxCount(24))
    );

    // 薯条实例
    public static final Item CHIPS = Registry.register(
        Registries.ITEM,
        id("chips"),
        new ChipsItem(new FabricItemSettings())
    );

    // 炸鱼薯条实例
    public static final Item FISH_AND_CHIPS = Registry.register(
        Registries.ITEM,
        id("fish_and_chips"),
        new FishAndChipsItem(new FabricItemSettings())
    );

    // 苔藓实例
    public static final Item MOSS = Registry.register(
        Registries.ITEM,
        id("moss"),
        new MossItem(new FabricItemSettings())
    );

    // 粗制蛋糕实例
    public static final Item CRUDE_CAKE = Registry.register(
        Registries.ITEM,
        id("crude_cake"),
        new CrudeCakeItem(new FabricItemSettings())
    );

    // 柠檬实例
    public static final Item LEMON = Registry.register(
        Registries.ITEM,
        id("lemon"),
        new LemonItem(new FabricItemSettings())
    );

    // 柠檬水实例
    public static final Item LEMON_WATER = Registry.register(
        Registries.ITEM,
        id("lemon_water"),
        new LemonWaterItem(new FabricItemSettings().maxCount(24))
    );

    // 全糖柠檬水实例
    public static final Item FULL_SUGAR_LEMONADE = Registry.register(
        Registries.ITEM,
        id("full_sugar_lemonade"),
        new FullSugarLemonadeItem(new FabricItemSettings().maxCount(24))
    );

    // 柠檬泡菜实例
    public static final Item LEMON_PICKLE = Registry.register(
        Registries.ITEM,
        id("lemon_pickle"),
        new LemonPickleItem(new FabricItemSettings().maxCount(1))
    );

    // 奥尔良烤鸡实例
    public static final Item ORLEANS_ROASTED_CHICKEN = Registry.register(
        Registries.ITEM,
        id("orleans_roasted_chicken"),
        new OrleansRoastedChickenItem(new FabricItemSettings().maxCount(1))
    );

    // 柠檬鸡爪实例
    public static final Item LEMON_CHICKEN_FEET = Registry.register(
        Registries.ITEM,
        id("lemon_chicken_feet"),
        new LemonChickenFeetItem(new FabricItemSettings().maxCount(1))
    );

    // 海盐实例
    public static final Item SALT = Registry.register(
        Registries.ITEM,
        id("salt"),
        new com.hydroceder.hgbg.item.food.SaltItem(new FabricItemSettings())
    );

    // 酱油实例
    public static final Item SOY_SAUCE = Registry.register(
        Registries.ITEM,
        id("soy_sauce"),
        new com.hydroceder.hgbg.item.food.SoySauceItem(new FabricItemSettings().maxCount(24))
    );

    // 辣椒酱实例
    public static final Item CHILI_BOTTLE = Registry.register(
        Registries.ITEM,
        id("chili_bottle"),
        new com.hydroceder.hgbg.item.food.ChiliSauceItem(new FabricItemSettings().maxCount(24))
    );

    // 肉桂粉实例
    public static final Item CINNAMON = Registry.register(
        Registries.ITEM,
        id("cinnamon"),
        new com.hydroceder.hgbg.item.food.CinnamonItem(new FabricItemSettings())
    );

    public static void register() {
        LOGGER.info("Items registered successfully!");
    }
}
