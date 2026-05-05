package com.hydroceder.hgbg.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.util.Arm;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

public class SofaEntity extends LivingEntity {

    private static final TrackedData<Boolean> IS_BEING_PUSHED = DataTracker.registerData(SofaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> WAS_PLAYER_ATTACKED = DataTracker.registerData(SofaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    
    private int pushedTimer = 0;

    public SofaEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(1.0f);
        this.setNoGravity(false);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_BEING_PUSHED, false);
        this.dataTracker.startTracking(WAS_PLAYER_ATTACKED, false);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient()) {
            return ActionResult.SUCCESS;
        }

        if (!this.hasPassengers()) {
            this.dataTracker.set(IS_BEING_PUSHED, false);
            player.startRiding(this);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient()) {
            // 只有在没有乘客时才触发自动起飞！
            // 解决玩家乘坐期间沙发受伤，玩家下来后依然飞走的问题！
            if (!this.hasPassengers()) {
                this.dataTracker.set(IS_BEING_PUSHED, true);
                this.setNoGravity(true);
                this.pushedTimer = 200; // 10秒 = 200 ticks
            }
            
            // 记录是否被玩家攻击
            Entity attacker = source.getAttacker();
            if (attacker instanceof PlayerEntity) {
                this.dataTracker.set(WAS_PLAYER_ATTACKED, true);
            }
        }
        return super.damage(source, amount);
    }

    public boolean canBeControlledByRider() { return true; }

    @Override
    public void tick() {
        super.tick();

        boolean isPushed = this.dataTracker.get(IS_BEING_PUSHED);
        
        // 处理自动起飞计时器
        if (isPushed && !this.hasPassengers()) {
            if (this.pushedTimer > 0) {
                this.pushedTimer--;
                if (this.pushedTimer <= 0) {
                    // 计时器结束，停止自动起飞
                    this.dataTracker.set(IS_BEING_PUSHED, false);
                    this.setNoGravity(false);
                }
            }
        }

        // 控制重力：骑乘时不受重力，落地后恢复
        if (this.hasPassengers()) {
            this.setNoGravity(true);
        } else if (!this.dataTracker.get(IS_BEING_PUSHED)) {
            this.setNoGravity(false);
        }

        // 避免摔落伤害
        this.fallDistance = 0.0f;

        if (this.getFirstPassenger() instanceof PlayerEntity rider) {
            float riderYaw = rider.getYaw();

            this.setYaw(riderYaw);
            this.setBodyYaw(riderYaw);
            this.setHeadYaw(riderYaw);
            this.prevYaw = riderYaw;
            this.prevHeadYaw = riderYaw;
            this.prevBodyYaw = riderYaw;

            // 完整使用玩家朝向向量（包括垂直俯仰角），和推进术式一样
            Vec3d rotationVec = rider.getRotationVec(1.0f);
            double speed = 0.15;
            this.addVelocity(
                rotationVec.x * speed, 
                rotationVec.y * speed, 
                rotationVec.z * speed
            );
        } else if (this.dataTracker.get(IS_BEING_PUSHED)) {
            // 被攻击时，持续向当前朝向推进
            Vec3d rotationVec = this.getRotationVec(1.0f);
            double speed = 0.15;
            this.addVelocity(
                rotationVec.x * speed, 
                0.05, 
                rotationVec.z * speed
            );
        }
        
        // 生成粒子：无论是被攻击还是骑乘离地
        if ((this.dataTracker.get(IS_BEING_PUSHED) || (this.hasPassengers() && !this.isOnGround()))) {
            this.spawnBackwardParticles();
        }
        
        // 不在地面且有乘客时：持续给向上力
        if (this.hasPassengers() && !this.isOnGround()) {
            // 参考 hgbgct 飞升效果1级 (0.10)
            this.addVelocity(0.0, 0.05, 0.0);
        }
    }
    
    private void spawnBackwardParticles() {
        World world = this.getWorld();
        if (world.isClient()) {
            Vec3d backward = this.getRotationVec(1.0f).negate();
            Vec3d basePos = this.getPos().add(0, -0.5, 0);
            Random random = this.getRandom();
            
            // 营火烟雾粒子（10个/刻）
            for (int i = 0; i < 4; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 0.5;
                double offsetY = (random.nextDouble() - 0.5) * 0.3;
                double offsetZ = (random.nextDouble() - 0.5) * 0.5;
                
                double velX = backward.x * 0.2 + (random.nextDouble() - 0.5) * 0.1;
                double velY = 0.1 + random.nextDouble() * 0.1;
                double velZ = backward.z * 0.2 + (random.nextDouble() - 0.5) * 0.1;
                
                world.addParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    basePos.x + offsetX,
                    basePos.y + offsetY,
                    basePos.z + offsetZ,
                    velX, velY, velZ
                );
            }
            
            // LAVA 粒子（10个/刻）
            for (int i = 0; i < 5; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 0.6;
                double offsetY = (random.nextDouble() - 0.5) * 0.4;
                double offsetZ = (random.nextDouble() - 0.5) * 0.6;
                
                double velX = backward.x * 0.2 + (random.nextDouble() - 0.5) * 0.1;
                double velY = 0.15 + random.nextDouble() * 0.15;
                double velZ = backward.z * 0.2 + (random.nextDouble() - 0.5) * 0.1;
                
                world.addParticle(
                    ParticleTypes.LAVA,
                    basePos.x + offsetX,
                    basePos.y + offsetY,
                    basePos.z + offsetZ,
                    velX, velY, velZ
                );
            }
        }
    }
    
    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        if (this.hasPassenger(passenger)) {
            Vec3d pos = this.getPos().add(0, -0.25, 0);
            passenger.setPosition(pos.x, pos.y, pos.z);
        }
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return java.util.Collections.emptyList();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    public boolean shouldRenderName() {
        return false;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        
        // 如果不是被玩家攻击导致的死亡，造成爆炸
        if (!this.dataTracker.get(WAS_PLAYER_ATTACKED) && !this.getWorld().isClient()) {
            // TNT 威力的爆炸（4.0F）
            this.getWorld().createExplosion(
                this,
                this.getX(),
                this.getY() + 0.5,
                this.getZ(),
                4.0F,
                true,
                World.ExplosionSourceType.MOB
            );
            
            // 生成大量高速 LAVA 粒子模拟爆炸火星
            this.spawnExplosionParticles();
        }
    }
    
    private void spawnExplosionParticles() {
        World world = this.getWorld();
        
        if (world instanceof net.minecraft.server.world.ServerWorld) {
            net.minecraft.server.world.ServerWorld serverWorld = (net.minecraft.server.world.ServerWorld) world;
            Vec3d pos = this.getPos();
            Random random = this.getRandom();
            
            // 大量高速 LAVA 粒子（150个）
            for (int i = 0; i < 150; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 2.5;
                double offsetY = random.nextDouble() * 1.5;
                double offsetZ = (random.nextDouble() - 0.5) * 2.5;
                
                double velX = (random.nextDouble() - 0.5) * 10.0;
                double velY = random.nextDouble() * 8.0 + 1.0;
                double velZ = (random.nextDouble() - 0.5) * 10.0;
                
                serverWorld.spawnParticles(
                    ParticleTypes.LAVA,
                    pos.x + offsetX,
                    pos.y + offsetY,
                    pos.z + offsetZ,
                    1,
                    velX, velY, velZ,
                    1.0
                );
            }
        }
    }
}
