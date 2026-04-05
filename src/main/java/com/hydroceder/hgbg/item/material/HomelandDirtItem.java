package com.hydroceder.hgbg.item.material;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class HomelandDirtItem extends Item {
    public static final int COOLDOWN_TICKS = 20 * 60 * 20; // 20分钟（20 * 60秒 * 20ticks/秒）
    
    public HomelandDirtItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        return TypedActionResult.success(stack);
    }
    
    public static boolean tryUseHomelandDirt(PlayerEntity player, World world) {
        // 检查玩家是否持有故乡土壤
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof HomelandDirtItem) {
                if (!player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
                    // 触发效果
                    triggerEffect(player, world, stack);
                    return true;
                }
            }
        }
        
        // 检查副手
        ItemStack offHand = player.getOffHandStack();
        if (offHand.getItem() instanceof HomelandDirtItem) {
            if (!player.getItemCooldownManager().isCoolingDown(offHand.getItem())) {
                triggerEffect(player, world, offHand);
                return true;
            }
        }
        
        return false;
    }
    
    private static void triggerEffect(PlayerEntity player, World world, ItemStack stack) {
        // 播放不死图腾音效
        world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
        
        // 播放泥土破坏音效
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
        
        // 迸发粒子效果 - 使用 ServerWorld.spawnParticles 方法
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        
        if (world instanceof net.minecraft.server.world.ServerWorld) {
            net.minecraft.server.world.ServerWorld serverWorld = (net.minecraft.server.world.ServerWorld) world;
            
            // 发光粒子
            for (int i = 0; i < 20; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
                double offsetY = world.random.nextDouble() * 2.0;
                double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;
                serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.GLOW, x + offsetX, y + offsetY, z + offsetZ, 1, 0.0, 0.0, 0.0, 0.0);
            }
            
            // 附魔命中粒子
            for (int i = 0; i < 30; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
                double offsetY = world.random.nextDouble() * 2.0;
                double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;
                serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.ENCHANTED_HIT, x + offsetX, y + offsetY, z + offsetZ, 1, 0.0, 0.0, 0.0, 0.0);
            }
            
            // 樱花叶粒子
            for (int i = 0; i < 40; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 3.0;
                double offsetY = world.random.nextDouble() * 3.0;
                double offsetZ = (world.random.nextDouble() - 0.5) * 3.0;
                serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.CHERRY_LEAVES, x + offsetX, y + offsetY, z + offsetZ, 1, 0.0, -0.1, 0.0, 0.0);
            }
            
            // 触发进度
            if (player instanceof net.minecraft.server.network.ServerPlayerEntity) {
                com.hydroceder.hgbg.advancement.HomelandDirtTrigger.getInstance().trigger((net.minecraft.server.network.ServerPlayerEntity) player);
            }
        }
        
        // 恢复生命值
        player.setHealth(1.0F);
        
        // 清除所有负面效果
        player.clearStatusEffects();
        
        // 设置冷却
        player.getItemCooldownManager().set(stack.getItem(), COOLDOWN_TICKS);
    }
}
