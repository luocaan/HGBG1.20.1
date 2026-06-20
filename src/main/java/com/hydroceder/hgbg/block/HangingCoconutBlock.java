package com.hydroceder.hgbg.block;

import com.hydroceder.hgbg.damage.ModDamageTypes;
import com.hydroceder.hgbg.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.ItemScatterer;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 悬挂椰子方块
 * 生成在棕榈树上，1200 ticks后像沙子一样下落
 * 落地时对碰撞实作造成coconut_fall伤害
 * 根据落高掉落对应物品
 */
public class HangingCoconutBlock extends FallingBlock {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final int FALL_DELAY = 1200;
    private static final float COCONUT_DAMAGE = 4.0f;
    // 追踪每个椰子的起始Y坐标，用于计算实际落高
    private static final Map<UUID, Integer> START_Y_MAP = new ConcurrentHashMap<>();

    public HangingCoconutBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world instanceof ServerWorld sw) {
            sw.scheduleBlockTick(pos, this, FALL_DELAY);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient && world instanceof ServerWorld sw) {
            FallingBlockEntity entity = FallingBlockEntity.spawnFromBlock(sw, pos, state);
            START_Y_MAP.put(entity.getUuid(), pos.getY());
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (pos.getY() >= world.getBottomY()) {
            FallingBlockEntity entity = FallingBlockEntity.spawnFromBlock(world, pos, state);
            START_Y_MAP.put(entity.getUuid(), pos.getY());
        }
    }

    // onLanding 和 onDestroyedOnLanding 由 FallingBlockEntity 调用
    // 当落地点可放置方块时调用 onLanding，不可放置时调用 onDestroyedOnLanding
    
    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity entity) {
        world.removeBlock(pos, false);
        float fallDist = getFallDistanceFromEntity(entity, pos);
        handleLanding(world, pos, fallDist);
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity entity) {
        float fallDist = getFallDistanceFromEntity(entity, pos);
        handleLanding(world, pos, fallDist);
    }

    /**
     * 从追踪映射中读取起始Y，计算实际落高
     */
    private static float getFallDistanceFromEntity(FallingBlockEntity entity, BlockPos landedPos) {
        Integer startY = START_Y_MAP.remove(entity.getUuid());
        return startY != null ? Math.max(0, startY - landedPos.getY()) : 0;
    }

    // ==================== 私有工具方法 ====================

    /**
     * 统一的落地处理：应用伤害 + 掉落物品
     */
    private void handleLanding(World world, BlockPos pos, float fallDist) {
        applyCoconutDamage(world, pos);
        if (fallDist > 4.0f) {
            int count = 2 + world.random.nextInt(2);
            ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                new ItemStack(ModItems.COCONUT_BOWL, count));
        } else {
            ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                new ItemStack(ModItems.COCONUT));
        }
    }

    /**
     * 对落地位置的生物造成椰子摔落伤害
     */
    private void applyCoconutDamage(World world, BlockPos pos) {
        if (world.isClient) return;
        RegistryKey<DamageType> key = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, ModDamageTypes.COCONUT_FALL_ID);
        var entry = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getEntry(key);
        if (entry.isPresent()) {
            DamageSource source = new DamageSource(entry.get());
            Box box = new Box(pos);
            List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, box, e -> e.isAlive());
            for (LivingEntity target : targets) {
                target.damage(source, COCONUT_DAMAGE);
            }
        }
    }
}
