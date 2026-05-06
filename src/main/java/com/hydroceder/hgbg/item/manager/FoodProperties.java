package com.hydroceder.hgbg.item.manager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * 食物属性统一管理类
 * 集中管理所有食物/饮品的饥饿值、饱和度等属性
 * 便于维护和平衡调整
 */
public class FoodProperties {
    
    /**
     * 创建标准食物组件
     * @param hunger 饥饿值恢复量
     * @param saturationModifier 饱和度倍率
     * @return 食物组件构建器
     */
    public static FoodComponent.Builder createFood(int hunger, float saturationModifier) {
        return new FoodComponent.Builder()
            .hunger(hunger)
            .saturationModifier(saturationModifier);
    }
    
    /**
     * 创建可随时食用的食物组件
     * @param hunger 饥饿值恢复量
     * @param saturationModifier 饱和度倍率
     * @return 食物组件构建器
     */
    public static FoodComponent.Builder createAlwaysEdibleFood(int hunger, float saturationModifier) {
        return createFood(hunger, saturationModifier)
            .alwaysEdible();
    }
    
    /**
     * 处理碗装食物的碗返还逻辑
     * @param stack 食物物品栈
     * @param world 世界
     * @param user 食用者
     * @param result 食用后的结果物品栈
     * @return 处理后的结果物品栈
     */
    public static ItemStack handleBowlReturn(ItemStack stack, net.minecraft.world.World world, LivingEntity user, ItemStack result) {
        // 非创造模式下返回碗
        if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
            ItemStack bowlStack = new ItemStack(Items.BOWL);
            
            // 如果食用后结果为空（消耗了最后一个），直接返回碗
            if (result.isEmpty()) {
                return bowlStack;
            }
            
            // 使用Minecraft标准方法：优先放入背包，否则掉地上
            player.getInventory().offerOrDrop(bowlStack);
        }
        
        return result;
    }
    
    // ==================== 食物属性常量 ====================
    
    // 柠檬泡菜
    public static final int LEMON_PICKLE_HUNGER = 6;
    public static final float LEMON_PICKLE_SATURATION = 1.33f;
    
    // 培根
    public static final int BACON_HUNGER = 4;
    public static final float BACON_SATURATION = 2.0f;
    
    // 全糖柠檬水
    public static final int FULL_SUGAR_LEMONADE_HUNGER = 5;
    public static final float FULL_SUGAR_LEMONADE_SATURATION = 1.2f;
    
    // 柠檬水
    public static final int LEMON_WATER_HUNGER = 3;
    public static final float LEMON_WATER_SATURATION = 1.0f;
    
    // 巧克力牛奶
    public static final int CHOCOLATE_MILK_HUNGER = 4;
    public static final float CHOCOLATE_MILK_SATURATION = 1.5f;
    
    // 奶油蘑菇汤
    public static final int CREAMY_MUSHROOM_SOUP_HUNGER = 7;
    public static final float CREAMY_MUSHROOM_SOUP_SATURATION = 1.43f;
    
    // 曲奇类
    public static final int COPPER_COOKIE_HUNGER = 3;
    public static final float COPPER_COOKIE_SATURATION = 1.33f;
    public static final int IRON_COOKIE_HUNGER = 4;
    public static final float IRON_COOKIE_SATURATION = 1.25f;
    public static final int DIAMOND_COOKIE_HUNGER = 8;
    public static final float DIAMOND_COOKIE_SATURATION = 2.0f;
    
    // 生/熟食物
    public static final int RAW_BACON_HUNGER = 2;
    public static final float RAW_BACON_SATURATION = 2.0f;
    public static final int RAW_SHRIMP_HUNGER = 2;
    public static final float RAW_SHRIMP_SATURATION = 0.5f;
    public static final int COOKED_SHRIMP_HUNGER = 5;
    public static final float COOKED_SHRIMP_SATURATION = 1.0f;
    
    // 其他食物
    public static final int CHIPS_HUNGER = 3;
    public static final float CHIPS_SATURATION = 0.6f;
    public static final int FISH_AND_CHIPS_HUNGER = 10;
    public static final float FISH_AND_CHIPS_SATURATION = 1.2f;
    public static final int BAGUETTE_HUNGER = 15;
    public static final float BAGUETTE_SATURATION = 0.8f;
    public static final int LEMON_HUNGER = 2;
    public static final float LEMON_SATURATION = 1.5f;
    public static final int MOSS_HUNGER = 0;
    public static final float MOSS_SATURATION = 0.0f;
    public static final int CRUDE_CAKE_HUNGER = 8;
    public static final float CRUDE_CAKE_SATURATION = 0.75f;
    
    // 饮品类（可乐、茶等）
    public static final int APPLE_COLA_HUNGER = 5;
    public static final float APPLE_COLA_SATURATION = 1.0f;
    public static final int BERRY_COLA_HUNGER = 5;
    public static final float BERRY_COLA_SATURATION = 1.0f;
    public static final int SHINY_BERRY_COLA_HUNGER = 6;
    public static final float SHINY_BERRY_COLA_SATURATION = 1.0f;
    public static final int LEMON_COLA_HUNGER = 5;
    public static final float LEMON_COLA_SATURATION = 1.0f;
    public static final int DAISY_FLOWER_TEA_HUNGER = 4;
    public static final float DAISY_FLOWER_TEA_SATURATION = 1.2f;
    public static final int FLOWER_TEA_HUNGER = 4;
    public static final float FLOWER_TEA_SATURATION = 1.2f;
    public static final int HOT_CHOCOLATE_HUNGER = 6;
    public static final float HOT_CHOCOLATE_SATURATION = 1.67f;
    
    // 果酱类
    public static final int JAM_HUNGER = 4;
    public static final float JAM_SATURATION = 1.5f;
    public static final int JAM_BREAD_HUNGER = 6;
    public static final float JAM_BREAD_SATURATION = 1.17f;
    public static final int JAM_BREAD_FOOD_HUNGER = 8;
    public static final float JAM_BREAD_FOOD_SATURATION = 1.5f;
    public static final int SHINY_BERRY_JAM_HUNGER = 4;
    public static final float SHINY_BERRY_JAM_SATURATION = 1.5f;
    
    // 其他
    public static final int GOODBRO_HUNGER = 20;
    public static final float GOODBRO_SATURATION = 1.0f;
    public static final int MAYONNAISE_HUNGER = 2;
    public static final float MAYONNAISE_SATURATION = 1.0f;
    public static final int MUSHROOM_OIL_HUNGER = 1;
    public static final float MUSHROOM_OIL_SATURATION = 2.0f;
    public static final int SHINY_BERRY_JAM_BREAD_HUNGER = 8;
    public static final float SHINY_BERRY_JAM_BREAD_SATURATION = 1.5f;
    
    // 玫瑰果酱
    public static final int ROSE_JAM_HUNGER = 4;
    public static final float ROSE_JAM_SATURATION = 1.5f;
    
    // 烤玫瑰酱面包
    public static final int ROSE_JAM_BREAD_HUNGER = 8;
    public static final float ROSE_JAM_BREAD_SATURATION = 1.5f;
    
    // 奥尔良烤鸡
    public static final int ORLEANS_ROASTED_CHICKEN_HUNGER = 10;
    public static final float ORLEANS_ROASTED_CHICKEN_SATURATION = 1.4f;
    
    // 柠檬鸡爪
    public static final int LEMON_CHICKEN_FEET_HUNGER = 6;
    public static final float LEMON_CHICKEN_FEET_SATURATION = 2.0f;

    // 鸡翅
    public static final int CHICKEN_WING_HUNGER = 3;
    public static final float CHICKEN_WING_SATURATION = 4.0f;

    // 鸡腿
    public static final int CHICKEN_LEG_HUNGER = 4;
    public static final float CHICKEN_LEG_SATURATION = 5.0f;
    
    // 酱油
    public static final int SOY_SAUCE_HUNGER = 2;
    public static final float SOY_SAUCE_SATURATION = 1.0f;
    
    // 辣椒酱
    public static final int CHILI_SAUCE_HUNGER = 2;
    public static final float CHILI_SAUCE_SATURATION = 1.0f;

    // 冰激凌
    public static final int ICECREAM_HUNGER = 4;
    public static final float ICECREAM_SATURATION = 3.0f;

    //豪华炸虾芭菲
    public static final int FRIED_SHRIMP_PARFAIT_HUNGER = 20;
    public static final float FRIED_SHRIMP_PARFAIT_SATURATION = 0.5f;

    //炸虾芭菲
    public static final int COMMON_FRIED_SHRIMP_PARFAIT_HUNGER = 20;
    public static final float COMMON_FRIED_SHRIMP_PARFAIT_SATURATION = 0.5f;
}
