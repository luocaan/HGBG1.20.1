package com.hydroceder.hgbg.item.tool;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import com.hydroceder.hgbg.enchantment.EnhancedInsecticideEnchantment;
import com.hydroceder.hgbg.enchantment.NozzleImprovementEnchantment;
import com.hydroceder.hgbg.enchantment.RecipeImprovementEnchantment;
import com.hydroceder.hgbg.effect.CalmnessEffect;
import com.hydroceder.hgbg.sound.ModSounds;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class InsecticideItem extends Item {

    private static final int TICK_INTERVAL = 20;
    private static final float BASE_DAMAGE = 7.0f;
    private static final double RADIUS = 4.0;
    private static final double RAY_RANGE = 11.0;
    private static final int MUTATION_THRESHOLD = 600;
    private static final int MUTATION_RESET_TICKS = 200;
    private static final int BUFF_DURATION = 12000;

    private static class MutationData {
        int sprayTicks;
        long lastSprayTick;
        boolean triggered;

        MutationData() {
            this.sprayTicks = 0;
            this.lastSprayTick = -1;
            this.triggered = false;
        }
    }

    private static final Map<UUID, MutationData> mutationTrackers = new HashMap<>();

    public InsecticideItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getEnchantability() {
        return 15;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.GLISTERING_MELON_SLICE);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
            ModSounds.INSECTICIDE_START, SoundCategory.PLAYERS, 0.8f, 1.0f);
        if (EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack) > 0) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1.0f, 0.8f + world.random.nextFloat() * 0.4f);
            for (int i = 0; i < 20; i++) {
                double ox = (world.random.nextDouble() - 0.5) * 2.0;
                double oy = world.random.nextDouble() * 1.5;
                double oz = (world.random.nextDouble() - 0.5) * 2.0;
                double vx = (world.random.nextDouble() - 0.5) * 0.3;
                double vy = world.random.nextDouble() * 0.15;
                double vz = (world.random.nextDouble() - 0.5) * 0.3;
                world.addParticle(ParticleTypes.LAVA,
                    user.getX() + ox, user.getY() + oy, user.getZ() + oz,
                    vx, vy, vz);
            }
        }
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
        boolean hasNozzle = EnchantmentHelper.getLevel(NozzleImprovementEnchantment.INSTANCE, stack) > 0;
        Vec3d lookVec = player.getRotationVector();

        if (world.isClient) {
            if (hasNozzle) {
                for (int i = 0; i < 5; i++) {
                    double dist = world.random.nextDouble() * RAY_RANGE;
                    double px = player.getX() + lookVec.x * dist;
                    double py = player.getEyeY() + lookVec.y * dist;
                    double pz = player.getZ() + lookVec.z * dist;

                    world.addParticle(ParticleTypes.CLOUD,
                        px + (world.random.nextDouble() - 0.5) * 0.4,
                        py + (world.random.nextDouble() - 0.5) * 0.4,
                        pz + (world.random.nextDouble() - 0.5) * 0.4,
                        lookVec.x * 0.6, 0.02, lookVec.z * 0.6);
                }
            } else {
                Vec3d pos = player.getEyePos().add(lookVec.x * 0.8, lookVec.y * 0.5, lookVec.z * 0.8);

                for (int i = 0; i < 3; i++) {
                    world.addParticle(ParticleTypes.CLOUD,
                        pos.x + (world.random.nextDouble() - 0.5) * 0.5,
                        pos.y + (world.random.nextDouble() - 0.5) * 0.5,
                        pos.z + (world.random.nextDouble() - 0.5) * 0.5,
                        lookVec.x * 0.3, 0.02, lookVec.z * 0.3);
                }
            }
        }

        if (elapsed == 16 || (elapsed >= 19 && (elapsed - 19) % 3 == 0)) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.INSECTICIDE_USE, SoundCategory.PLAYERS, 0.6f, 1.0f);
            if (EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack) > 0) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 0.5f, 0.9f + world.random.nextFloat() * 0.2f);
            }
        }

        if (!world.isClient && remainingUseTicks % TICK_INTERVAL == 0) {
            boolean hasEnhanced = EnchantmentHelper.getLevel(EnhancedInsecticideEnchantment.INSTANCE, stack) > 0;
            boolean hasRecipeImprove = EnchantmentHelper.getLevel(RecipeImprovementEnchantment.INSTANCE, stack) > 0;
            int flameLevel = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, stack);
            int baneLevel = EnchantmentHelper.getLevel(Enchantments.BANE_OF_ARTHROPODS, stack);
            int smiteLevel = EnchantmentHelper.getLevel(Enchantments.SMITE, stack);

            if (hasNozzle) {
                applyRayDamage(world, player, stack, hasEnhanced, hasRecipeImprove, flameLevel, baneLevel, smiteLevel, lookVec);
            } else {
                applyAoeDamage(world, player, stack, hasEnhanced, hasRecipeImprove, flameLevel, baneLevel, smiteLevel);
            }

            stack.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
        }
    }

    private void applyAoeDamage(World world, PlayerEntity player, ItemStack stack,
                                  boolean hasEnhanced, boolean hasRecipeImprove, int flameLevel, int baneLevel, int smiteLevel) {
        List<LivingEntity> targets = world.getEntitiesByClass(
            LivingEntity.class,
            new Box(
                player.getX() - RADIUS, player.getY() - RADIUS, player.getZ() - RADIUS,
                player.getX() + RADIUS, player.getY() + RADIUS, player.getZ() + RADIUS
            ),
            e -> e.isAlive()
                && e != player
                && !(hasRecipeImprove && e instanceof Tameable && ((Tameable) e).getOwnerUuid() != null)
                && !(hasRecipeImprove && e instanceof FoxEntity)
                && (!hasRecipeImprove || !(e instanceof PlayerEntity))
                && (hasEnhanced || (!(e instanceof PlayerEntity)
                    && (e instanceof HostileEntity
                    || e instanceof BeeEntity
                    || e instanceof BatEntity
                    || e instanceof SlimeEntity
                    || e instanceof FrogEntity)))
        );

        for (LivingEntity target : targets) {
            float damage = hasEnhanced ? 14.0f : BASE_DAMAGE;
            applyEnchantmentEffects(target, damage, baneLevel, smiteLevel, flameLevel, player);

            target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 120, 0));
            if (hasEnhanced) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60, 0));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
            }

            trackMutation(world, target, player);
        }

        if (flameLevel > 0) {
            BlockPos playerPos = player.getBlockPos();
            BlockPos cobwebStart = playerPos.add(-(int)RADIUS, -(int)RADIUS, -(int)RADIUS);
            BlockPos cobwebEnd = playerPos.add((int)RADIUS, (int)RADIUS, (int)RADIUS);
            for (BlockPos pos : BlockPos.iterate(cobwebStart, cobwebEnd)) {
                igniteCobweb(world, pos);
            }
        }
    }

    private void applyRayDamage(World world, PlayerEntity player, ItemStack stack,
                                 boolean hasEnhanced, boolean hasRecipeImprove, int flameLevel, int baneLevel, int smiteLevel,
                                 Vec3d lookVec) {
        Vec3d start = player.getEyePos();
        Vec3d end = start.add(lookVec.multiply(RAY_RANGE));

        RaycastContext context = new RaycastContext(
            start, end,
            RaycastContext.ShapeType.COLLIDER,
            FluidHandling.NONE,
            player
        );

        HitResult result = world.raycast(context);

        double maxDist = (result.getType() == HitResult.Type.MISS) ? RAY_RANGE :
            result.getPos().distanceTo(start);

        Vec3d rayEnd = start.add(lookVec.multiply(RAY_RANGE));
        Vec3d min = new Vec3d(Math.min(start.x, rayEnd.x), Math.min(start.y, rayEnd.y), Math.min(start.z, rayEnd.z));
        Vec3d max = new Vec3d(Math.max(start.x, rayEnd.x), Math.max(start.y, rayEnd.y), Math.max(start.z, rayEnd.z));
        List<LivingEntity> entitiesAlongRay = world.getEntitiesByClass(
            LivingEntity.class,
            new Box(min.x, min.y, min.z, max.x, max.y, max.z).expand(1.0),
            e -> e.isAlive()
                && e != player
                && !(hasRecipeImprove && e instanceof Tameable && ((Tameable) e).getOwnerUuid() != null)
                && !(hasRecipeImprove && e instanceof FoxEntity)
                && (!hasRecipeImprove || !(e instanceof PlayerEntity))
                && (hasEnhanced || (!(e instanceof PlayerEntity)
                    && (e instanceof HostileEntity
                    || e instanceof BeeEntity
                    || e instanceof BatEntity
                    || e instanceof SlimeEntity
                    || e instanceof FrogEntity)))
        );

        for (LivingEntity target : entitiesAlongRay) {
            Vec3d targetCenter = target.getBoundingBox().getCenter();
            double distToTarget = targetCenter.distanceTo(start);
            if (distToTarget > maxDist + 1.0) continue;

            float damage = hasEnhanced ? 14.0f : BASE_DAMAGE;
            applyEnchantmentEffects(target, damage, baneLevel, smiteLevel, flameLevel, player);

            target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 120, 0));
            if (hasEnhanced) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60, 0));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
            }

            trackMutation(world, target, player);
        }

        if (flameLevel > 0) {
            Vec3d direction = lookVec.normalize();
            for (double d = 1.0; d < maxDist; d += 1.0) {
                BlockPos checkPos = BlockPos.ofFloored(start.add(direction.multiply(d)));
                igniteCobweb(world, checkPos);
            }
        }
    }

    private void applyEnchantmentEffects(LivingEntity target, float damage,
                                          int baneLevel, int smiteLevel, int flameLevel,
                                          PlayerEntity player) {
        if (baneLevel > 0 && target.getGroup() == EntityGroup.ARTHROPOD) {
            damage += 2.5f * baneLevel + 30.0f;
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, Math.min(baneLevel, 3)));
        }

        if (smiteLevel > 0 && target.getGroup() == EntityGroup.UNDEAD) {
            damage += 2.5f * smiteLevel + 15.0f;
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, Math.min(smiteLevel, 3)));
        }

        target.damage(player.getDamageSources().mobAttack(player), damage);

        if (flameLevel > 0) {
            target.setOnFireFor(2 + flameLevel * 2);
        }
    }

    private void igniteCobweb(World world, BlockPos pos) {
        if (world.getBlockState(pos).isOf(Blocks.COBWEB)) {
            BlockPos[] neighbors = { pos.up(), pos.north(), pos.south(), pos.east(), pos.west() };
            for (BlockPos neighbor : neighbors) {
                if (world.isAir(neighbor)) {
                    world.setBlockState(neighbor,
                        AbstractFireBlock.getState(world, neighbor), 11);
                    break;
                }
            }
        }
    }

    private void trackMutation(World world, LivingEntity target, PlayerEntity player) {
        UUID uuid = target.getUuid();
        MutationData data = mutationTrackers.computeIfAbsent(uuid, k -> new MutationData());

        if (!target.isAlive()) {
            mutationTrackers.remove(uuid);
            return;
        }

        long currentTick = world.getTime();

        if (data.lastSprayTick >= 0 && (currentTick - data.lastSprayTick) > MUTATION_RESET_TICKS) {
            data.sprayTicks = 0;
            data.triggered = false;
        }

        if (data.triggered) return;

        data.lastSprayTick = currentTick;
        data.sprayTicks += TICK_INTERVAL;

        if (data.sprayTicks >= MUTATION_THRESHOLD) {
            data.triggered = true;
            applyMutationBuffs(target);

            if (target instanceof ServerPlayerEntity serverPlayer) {
                grantMutationAchievement(serverPlayer);
            }
        }
    }

    private void applyMutationBuffs(LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, BUFF_DURATION, 2));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, BUFF_DURATION, 2));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, BUFF_DURATION, 0));
        target.addStatusEffect(new StatusEffectInstance(CalmnessEffect.INSTANCE, BUFF_DURATION, 0));
    }

    private void grantMutationAchievement(ServerPlayerEntity player) {
        Advancement advancement = player.server.getAdvancementLoader()
            .get(new Identifier("hunger-begone", "mutation"));
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);
            if (!progress.isDone()) {
                player.getAdvancementTracker().grantCriterion(advancement, "mutation");
            }
        }
    }
}
