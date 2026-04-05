package com.hydroceder.hgbg.item.material;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

/**
 * 锅武器的自定义工具材料
 * 设置耐久度、基础攻击伤害、挖掘速度等属性
 */
public class PanMaterial implements ToolMaterial {
    public static final PanMaterial INSTANCE = new PanMaterial();
    
    @Override
    public int getDurability() {
        // 耐久度，参考钻石工具：1561
        return 1561;
    }
    
    @Override
    public float getMiningSpeedMultiplier() {
        // 挖掘速度倍率
        return 8.0f;
    }
    
    @Override
    public float getAttackDamage() {
        // 基础攻击伤害，配合额外伤害7，总计10
        return 3.0f;
    }
    
    @Override
    public int getMiningLevel() {
        // 挖掘等级，3 = 钻石级别
        return 3;
    }
    
    @Override
    public int getEnchantability() {
        // 附魔能力
        return 15;
    }
    
    @Override
    public Ingredient getRepairIngredient() {
        // 修复物品，使用铁锭
        return Ingredient.ofItems(Items.IRON_INGOT);
    }
}