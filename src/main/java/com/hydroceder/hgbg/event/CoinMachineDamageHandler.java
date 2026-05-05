package com.hydroceder.hgbg.event;

import com.hydroceder.hgbg.block.ModBlocks;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CoinMachineDamageHandler {

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof PlayerEntity player) {
                if (isWearingCoinMachine(player)) {
                    if (!player.isInvulnerable() && player.hurtTime <= 0 && amount > 0) {
                        playHarpEffect(player);
                    }
                }
            }
            return true;
        });
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
            net.minecraft.sound.SoundCategory.PLAYERS,
            1.0f, pitch1);

        world.playSound(null, x, y, z,
            SoundEvents.BLOCK_NOTE_BLOCK_HARP.value(),
            net.minecraft.sound.SoundCategory.PLAYERS,
            1.0f, pitch2);

        for (int i = 0; i < 2; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 0.8;
            double offsetZ = (random.nextDouble() - 0.5) * 0.8;
            
            double particleX = x + offsetX;
            double particleY = y + 0.5;
            double particleZ = z + offsetZ;

            ((net.minecraft.server.world.ServerWorld) world).spawnParticles(
                net.minecraft.particle.ParticleTypes.NOTE,
                particleX, particleY, particleZ,
                1,
                0.0, 0.3, 0.0,
                0.01
            );
        }
    }

    private static float getPitchFromNote(int note) {
        return (float) Math.pow(2.0, (note - 12) / 12.0);
    }
}
