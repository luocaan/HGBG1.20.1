package com.hydroceder.hgbg.damage;

import net.minecraft.util.Identifier;

public class ModDamageTypes {
    // 伤害类型 ID，对应 data/<namespace>/damage_type/<path>.json
    public static final Identifier CRUSHING_ID = new Identifier("hunger-begone", "crushing");
    public static final Identifier COCONUT_FALL_ID = new Identifier("hunger-begone", "coconut_fall");

    public static void register() {
        // 数据驱动，无需动态注册
        // 护甲/附魔/抗性绕过通过 tags 系统实现（在游戏中自动应用）：
        //   data/minecraft/tags/damage_type/bypasses_armor.json
        //   data/minecraft/tags/damage_type/bypasses_resistance.json
        //   data/minecraft/tags/damage_type/bypasses_enchantments.json
        // 无需任何事件监听或 Mixin 干预。
    }
}
