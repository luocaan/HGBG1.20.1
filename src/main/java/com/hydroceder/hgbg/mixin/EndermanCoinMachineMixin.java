package com.hydroceder.hgbg.mixin;

import com.hydroceder.hgbg.block.ModBlocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(EndermanEntity.class)
public class EndermanCoinMachineMixin {

    private static final Map<UUID, Long> LAST_TRIGGER_TIME = new ConcurrentHashMap<>();
    private static final long COOLDOWN_TICKS = 20L;

    @Inject(at = @At("RETURN"), method = "isPlayerStaring", cancellable = true)
    private void onIsPlayerStaring(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && isWearingCoinMachine(player)) {
            long currentTime = player.getWorld().getTime();
            UUID playerId = player.getUuid();

            Long lastTime = LAST_TRIGGER_TIME.get(playerId);
            if (lastTime == null || (currentTime - lastTime) >= COOLDOWN_TICKS) {
                playHarpEffect(player);
                LAST_TRIGGER_TIME.put(playerId, currentTime);
            }

            cir.setReturnValue(false);
        }
    }

    private static boolean isWearingCoinMachine(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).getItem() == ModBlocks.COIN_OPERATED_MACHINE.asItem();
    }

    private static void playHarpEffect(PlayerEntity player) {
        World world = player.getWorld();
        Random random = world.getRandom();

        int note1 = random.nextInt(25);
        float pitch1 = getPitchFromNote(note1);

        int note2 = random.nextInt(25);
        float pitch2 = getPitchFromNote(note2);

        double x = player.getX();
        double y = player.getY() + 2.0;
        double z = player.getZ();

        world.playSound(null, x, y, z,
            SoundEvents.BLOCK_NOTE_BLOCK_HARP.value(),
            SoundCategory.PLAYERS,
            1.0f, pitch1);

        world.playSound(null, x, y, z,
            SoundEvents.BLOCK_NOTE_BLOCK_HARP.value(),
            SoundCategory.PLAYERS,
            1.0f, pitch2);

        for (int i = 0; i < 2; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 0.8;
            double offsetZ = (random.nextDouble() - 0.5) * 0.8;

            double particleX = x + offsetX;
            double particleY = y + 0.5;
            double particleZ = z + offsetZ;

            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                    ParticleTypes.NOTE,
                    particleX, particleY, particleZ,
                    1,
                    0.0, 0.3, 0.0,
                    0.01
                );
            }
        }
    }

    private static float getPitchFromNote(int note) {
        return (float) Math.pow(2.0, (note - 12) / 12.0);
    }
}