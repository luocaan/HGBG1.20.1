package com.hydroceder.hgbg.item;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;

/**
 * 定义模组中所有可放置食物的饥饿值和饱和度组件
 * 集中管理可放置食物属性配置，避免硬编码饥饿值
 */
public class ModFoodComponents {

    public static final FoodComponent APPLE_FRUIT_BOWL = new FoodComponent.Builder()
            .hunger(16)
            .saturationModifier(18.0f)
            .build();

    public static final FoodComponent BREAD_PLATE = new FoodComponent.Builder()
            .hunger(15)
            .saturationModifier(18.0f)
            .build();

    public static final FoodComponent CHORUS_FRUIT_BOWL = new FoodComponent.Builder()
            .hunger(8)
            .saturationModifier(20.0f)
            .build();

    public static final FoodComponent ORLEANS_ROASTED_CHICKEN = new FoodComponent.Builder()
            .hunger(5)
            .saturationModifier(14.0f)
            .build();
}
