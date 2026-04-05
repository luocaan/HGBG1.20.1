package com.hydroceder.hgbg.item.tool;

import com.hydroceder.hgbg.item.material.PanMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

/**
 * 锅铲武器类
 */
public class SpatulaItem extends SwordItem {
    private static final int ADDITIONAL_ATTACK_DAMAGE = 4;
    
    private static final float ATTACK_SPEED = -3.2f;
    
    public SpatulaItem(ToolMaterial material, Item.Settings settings) {
        super(material, ADDITIONAL_ATTACK_DAMAGE, ATTACK_SPEED, settings);
    }
}
