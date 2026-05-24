package com.hydroceder.hgbg.item.tool;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.hydroceder.hgbg.sound.ModSounds;

import java.util.List;

public class InsecticideItem extends Item {

    private static final int TICK_INTERVAL = 20;
    private static final float DAMAGE = 12.0f;
    private static final double RADIUS = 4.0;

    public InsecticideItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
            ModSounds.INSECTICIDE_START, SoundCategory.PLAYERS, 0.8f, 1.0f);
        return TypedActionResult.consume(stack);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return;
        }

        int elapsed = getMaxUseTime(stack) - remainingUseTicks;

        if (world.isClient) {
            Vec3d lookVec = player.getRotationVector();
            double px = player.getX() + lookVec.x * 0.8;
            double py = player.getEyeY() + lookVec.y * 0.5;
            double pz = player.getZ() + lookVec.z * 0.8;

            for (int i = 0; i < 3; i++) {
                world.addParticle(ParticleTypes.CLOUD,
                    px + (world.random.nextDouble() - 0.5) * 0.5,
                    py + (world.random.nextDouble() - 0.5) * 0.5,
                    pz + (world.random.nextDouble() - 0.5) * 0.5,
                    lookVec.x * 0.3, 0.02, lookVec.z * 0.3);
            }
        }

        if (elapsed == 16 || (elapsed >= 19 && (elapsed - 19) % 3 == 0)) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.INSECTICIDE_USE, SoundCategory.PLAYERS, 0.6f, 1.0f);
        }

        // 每20 tick消耗1耐久，对周围4格内所有敌对生物造成12点伤害并赋予发光效果
        if (!world.isClient && remainingUseTicks % TICK_INTERVAL == 0) {
            List<HostileEntity> targets = world.getEntitiesByClass(
                HostileEntity.class,
                new Box(
                    player.getX() - RADIUS, player.getY() - RADIUS, player.getZ() - RADIUS,
                    player.getX() + RADIUS, player.getY() + RADIUS, player.getZ() + RADIUS
                ),
                e -> e.isAlive()
            );

            for (HostileEntity target : targets) {
                target.damage(player.getDamageSources().mobAttack(player), DAMAGE);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 120, 0));
            }

            stack.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
        }
    }
}
