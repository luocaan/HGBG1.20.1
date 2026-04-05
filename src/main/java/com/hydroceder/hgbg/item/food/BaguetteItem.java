package com.hydroceder.hgbg.item.food;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

/**
 * 法棍面包类
 * 继承自斧头，可食用但会伤害牙齿
 * 伤害12点，攻速-3.4f
 */
public class BaguetteItem extends AxeItem {
    private static final int ADDITIONAL_ATTACK_DAMAGE = 9;
    private static final float ATTACK_SPEED = -3.4f;

    public BaguetteItem(ToolMaterial material, FabricItemSettings settings) {
        super(material, ADDITIONAL_ATTACK_DAMAGE, ATTACK_SPEED, settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 80;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) user;
            
            player.damage(player.getDamageSources().generic(), 1.0f);
            player.sendMessage(Text.translatable("item.hunger-begone.baguette.too_hard"), true);
            
            player.getHungerManager().add(15, 12.0f);
        }
        
        stack.decrement(1);
        return stack;
    }


}
