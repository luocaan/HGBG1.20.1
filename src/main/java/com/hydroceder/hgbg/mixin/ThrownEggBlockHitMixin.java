package com.hydroceder.hgbg.mixin;

import com.hydroceder.hgbg.advancement.PlantEggplantTrigger;
import com.hydroceder.hgbg.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EggEntity.class)
public class ThrownEggBlockHitMixin {

    @Inject(method = "onCollision", at = @At("HEAD"))
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        EggEntity egg = (EggEntity) (Object) this;
        World world = egg.getWorld();
        if (world.isClient()) {
            return;
        }

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos hitPos = blockHit.getBlockPos();
        BlockState hitState = world.getBlockState(hitPos);

        if (hitState.getBlock() instanceof FarmlandBlock) {
            BlockPos abovePos = hitPos.up();
            BlockState aboveState = world.getBlockState(abovePos);
            if (aboveState.isAir()) {
                world.setBlockState(abovePos, ModBlocks.EGGPLANT_CROP.getDefaultState());
                if (egg.getOwner() instanceof ServerPlayerEntity player) {
                    PlantEggplantTrigger.getInstance().trigger(player);
                }
            }
        }
    }
}