package com.hydroceder.hgbg.entity;

import com.hydroceder.hgbg.damage.ModDamageTypes;
import com.hydroceder.hgbg.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

/**
 * 投掷椰子实体
 * 击中实体时造成4点coconut_fall伤害，击中任何物体时掉落2个椰子碗
 */
public class CoconutProjectile extends ThrownItemEntity {

    private static final float DAMAGE = 4.0f;
    private boolean hasCollided = false;

    public CoconutProjectile(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public CoconutProjectile(EntityType<? extends ThrownItemEntity> entityType, LivingEntity owner, World world) {
        super(entityType, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.COCONUT;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        // 防止重复碰撞
        if (hasCollided) return;
        hasCollided = true;

        super.onCollision(hitResult);
        if (this.getWorld().isClient) {
            this.discard();
            return;
        }

        // 碰撞反馈音效
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
            SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f);

        // 对实体造成伤害
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) hitResult).getEntity();
            if (target.isAlive()) {
                RegistryKey<DamageType> key = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, ModDamageTypes.COCONUT_FALL_ID);
                var entry = this.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getEntry(key);
                if (entry.isPresent()) {
                    DamageSource source = new DamageSource(entry.get(), this, this.getOwner());
                    target.damage(source, DAMAGE);
                }
            }
        }

        // 掉落2个椰子碗
        net.minecraft.entity.ItemEntity item = new net.minecraft.entity.ItemEntity(
            this.getWorld(), this.getX(), this.getY(), this.getZ(),
            new ItemStack(ModItems.COCONUT_BOWL, 2));
        item.setToDefaultPickupDelay();
        this.getWorld().spawnEntity(item);

        // 销毁实体
        this.discard();
    }
}
