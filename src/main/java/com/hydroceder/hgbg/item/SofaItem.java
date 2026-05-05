package com.hydroceder.hgbg.item;

import com.hydroceder.hgbg.entity.ModEntities;
import com.hydroceder.hgbg.entity.SofaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext;

public class SofaItem extends Item {
    public SofaItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        HitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);
        
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        if (!world.isClient) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            BlockPos pos = blockHit.getBlockPos().offset(blockHit.getSide());
            
            SofaEntity sofa = new SofaEntity(ModEntities.SOFA, world);
            sofa.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            sofa.setYaw(user.getYaw());
            sofa.prevYaw = user.getYaw();
            world.spawnEntity(sofa);

            if (!user.getAbilities().creativeMode) {
                user.getStackInHand(hand).decrement(1);
            }
        }

        return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
    }
}
