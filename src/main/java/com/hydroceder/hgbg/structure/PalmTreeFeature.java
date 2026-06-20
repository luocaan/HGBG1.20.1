package com.hydroceder.hgbg.structure;

import com.hydroceder.hgbg.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * 棕榈树世界生成特征
 * 在沙滩等热带生物群系生成棕榈树。
 * 树干5-8格高，有时略微弯曲，弯曲时底部有撑脚支撑。
 * 树冠为四层宽大的棕榈叶，呈7x7扁平展开。
 */
public class PalmTreeFeature extends Feature<DefaultFeatureConfig> {

    private static final int MIN_TRUNK_HEIGHT = 5;
    private static final int MAX_TRUNK_HEIGHT = 8;

    // 第三层（垂叶片）的8个位置偏移，相对树干中心
    private static final int[][] HANGING_OFFSETS = {
        {-1, -3}, {1, -3},
        {-3, -1}, {3, -1},
        {-3, 1},  {3, 1},
        {-1, 3},  {1, 3}
    };

    public PalmTreeFeature() {
        super(DefaultFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        // 主树：必须成功
        boolean mainTree = buildSingleTree(world, origin, random);
        if (!mainTree) {
            return false;
        }

        // 额外树：随机生成2-4棵构成棕榈树丛
        int extraCount = 2 + random.nextInt(3);
        for (int i = 0; i < extraCount; i++) {
            int offsetX = random.nextInt(9) - 4; // -4 ~ 4
            int offsetZ = random.nextInt(9) - 4;
            if (offsetX == 0 && offsetZ == 0) continue;
            if (Math.abs(offsetX) + Math.abs(offsetZ) < 3) continue; // 不要太近

            BlockPos clusterOrigin = origin.add(offsetX, 0, offsetZ);
            buildSingleTree(world, clusterOrigin, random);
        }

        return true;
    }

    /**
     * 在指定位置生成一棵棕榈树
     */
    private boolean buildSingleTree(StructureWorldAccess world, BlockPos origin, Random random) {

        // 获取地表位置
        BlockPos surfacePos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, origin).down();

        // 检查地表是否为沙子或草方块
        if (!isValidGround(world, surfacePos)) {
            return false;
        }

        int trunkHeight = MIN_TRUNK_HEIGHT + random.nextInt(MAX_TRUNK_HEIGHT - MIN_TRUNK_HEIGHT + 1);
        int checkHeight = trunkHeight + 4; // 为树冠预留更多空间

        // 检查树干和树冠空间是否充足
        for (int y = 1; y <= checkHeight; y++) {
            BlockPos checkPos = surfacePos.up(y);
            if (!world.isAir(checkPos) && !world.getBlockState(checkPos).isReplaceable()) {
                return false;
            }
        }
        // 额外检查树冠水平空间（7x7范围内）
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (dx == 0 && dz == 0) continue;
                for (int dy = trunkHeight; dy <= trunkHeight + 3; dy++) {
                    BlockPos checkPos = surfacePos.up(dy).add(dx, 0, dz);
                    if (!world.isAir(checkPos) && !world.getBlockState(checkPos).isReplaceable()) {
                        return false;
                    }
                }
            }
        }

        // 生成树干
        for (int y = 1; y <= trunkHeight; y++) {
            BlockPos trunkPos = surfacePos.up(y);
            world.setBlockState(trunkPos, ModBlocks.COCONUT_LOG.getDefaultState(), 3);
        }

        // 树干顶部弯曲（随机向一侧偏移1格），并添加底部撑脚
        int bendDir = random.nextInt(4);
        if (bendDir == 0) {
            // 向北倾斜
            bendTrunk(world, surfacePos, trunkHeight, 0, -1); // dx=0, dz=-1 (north)
            addTrunkSupport(world, surfacePos, -1, 0, 0, 1);  // side=west, opposite=south
            surfacePos = surfacePos.north();
        } else if (bendDir == 1) {
            // 向南倾斜
            bendTrunk(world, surfacePos, trunkHeight, 0, 1);  // dx=0, dz=1 (south)
            addTrunkSupport(world, surfacePos, -1, 0, 0, -1); // side=west, opposite=north
            surfacePos = surfacePos.south();
        } else if (bendDir == 2) {
            // 向西倾斜
            bendTrunk(world, surfacePos, trunkHeight, -1, 0); // dx=-1, dz=0 (west)
            addTrunkSupport(world, surfacePos, 0, -1, 1, 0);  // side=north, opposite=east
            surfacePos = surfacePos.west();
        } else {
            // 向东倾斜
            bendTrunk(world, surfacePos, trunkHeight, 1, 0);  // dx=1, dz=0 (east)
            addTrunkSupport(world, surfacePos, 0, -1, -1, 0); // side=north, opposite=west
            surfacePos = surfacePos.east();
        }

        // 树叶状态：设置为持久防止自然腐烂
        BlockState persistentLeafState = ModBlocks.COCONUT_LEAVES.getDefaultState()
                .with(LeavesBlock.PERSISTENT, true)
                .with(LeavesBlock.DISTANCE, 1);

        // 树冠中心位置（树干顶端）
        BlockPos canopyCenter = surfacePos.up(trunkHeight);
        // 树干延伸至树冠中心上方一格（即第三层的D位置）
        BlockPos logCenter = canopyCenter.up(1);
        world.setBlockState(logCenter, ModBlocks.COCONUT_LOG.getDefaultState(), 3);

        // === 第三层（垂叶片层）：与logCenter同层 ===
        for (int[] offset : HANGING_OFFSETS) {
            BlockPos leafPos = logCenter.add(offset[0], 0, offset[1]);
            if (world.isAir(leafPos) || world.getBlockState(leafPos).isReplaceable()) {
                world.setBlockState(leafPos, persistentLeafState, 3);
            }
        }

        // === 第二层 (logCenter.up(1))：7x7 ===
        int[][] layer2 = {
            {-1, -3}, {1, -3},
            {-2, -2}, {-1, -2}, {0, -2}, {1, -2}, {2, -2},
            {-3, -1}, {-2, -1}, {-1, -1}, {2, -1}, {3, -1},
            {-2, 0}, {2, 0},
            {-3, 1}, {-2, 1}, {-1, 1}, {1, 1}, {2, 1}, {3, 1},
            {-2, 2}, {-1, 2}, {0, 2}, {1, 2}, {2, 2},
            {-1, 3}, {1, 3}
        };
        for (int[] pos : layer2) {
            BlockPos leafPos = logCenter.add(pos[0], 1, pos[1]);
            if (world.isAir(leafPos) || world.getBlockState(leafPos).isReplaceable()) {
                world.setBlockState(leafPos, persistentLeafState, 3);
            }
        }
        // 第二层中心替换为原木
        world.setBlockState(logCenter.up(1), ModBlocks.COCONUT_LOG.getDefaultState(), 3);

        // === 顶层 (logCenter.up(2))：7x7 ===
        int[][] layer1 = {
            {-1, -2}, {1, -2},
            {-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {2, -1},
            {-1, 0}, {0, 0}, {1, 0},
            {-2, 1}, {-1, 1}, {0, 1}, {1, 1}, {2, 1},
            {-1, 2}, {1, 2}
        };
        for (int[] pos : layer1) {
            BlockPos leafPos = logCenter.add(pos[0], 2, pos[1]);
            if (world.isAir(leafPos) || world.getBlockState(leafPos).isReplaceable()) {
                world.setBlockState(leafPos, persistentLeafState, 3);
            }
        }

        // === 第四层（随机垂挂）：从8个垂叶位置选3个，向下一格 ===
        // 打乱顺序取前3个
        int[] indices = {0, 1, 2, 3, 4, 5, 6, 7};
        for (int i = 7; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = indices[i];
            indices[i] = indices[j];
            indices[j] = tmp;
        }
        for (int k = 0; k < 3; k++) {
            int[] offset = HANGING_OFFSETS[indices[k]];
            BlockPos leafPos = logCenter.add(offset[0], -1, offset[1]);
            if (world.isAir(leafPos) || world.getBlockState(leafPos).isReplaceable()) {
                world.setBlockState(leafPos, persistentLeafState, 3);
            }
        }

        // === 生成悬挂椰子：在树干顶部随机2-3个侧面 ===
        BlockPos trunkTop = canopyCenter; // 树干顶端位置
        // 4个水平方向
        Direction[] sides = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
        // 打乱顺序
        for (int i = 3; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Direction tmp = sides[i];
            sides[i] = sides[j];
            sides[j] = tmp;
        }
        int coconutCount = 2 + random.nextInt(2); // 2-3个
        for (int i = 0; i < coconutCount; i++) {
            Direction dir = sides[i];
            BlockPos coconutPos = trunkTop.offset(dir);
            if (world.isAir(coconutPos) || world.getBlockState(coconutPos).isReplaceable()) {
                world.setBlockState(coconutPos,
                    ModBlocks.HANGING_COCONUT.getDefaultState()
                        .with(net.minecraft.block.HorizontalFacingBlock.FACING, dir.getOpposite()),
                    3);
            }
        }

        return true;
    }

    /**
     * 将树干顶部2格向指定方向偏移，实现弯曲效果
     */
    private void bendTrunk(StructureWorldAccess world, BlockPos surfacePos, int trunkHeight, int dx, int dz) {
        BlockPos bendPos = surfacePos.up(trunkHeight - 1).add(dx, 0, dz);
        world.setBlockState(bendPos, ModBlocks.COCONUT_LOG.getDefaultState(), 3);
        world.setBlockState(surfacePos.up(trunkHeight - 1), Blocks.AIR.getDefaultState(), 3);
        BlockPos topBendPos = surfacePos.up(trunkHeight).add(dx, 0, dz);
        world.setBlockState(topBendPos, ModBlocks.COCONUT_LOG.getDefaultState(), 3);
        world.setBlockState(surfacePos.up(trunkHeight), Blocks.AIR.getDefaultState(), 3);
    }

    /**
     * 在树干底部添加撑脚：侧面一格 + 倾斜反方向一格
     */
    private void addTrunkSupport(StructureWorldAccess world, BlockPos surfacePos, int sideDx, int sideDz, int oppDx, int oppDz) {
        // 侧面撑脚
        BlockPos sidePos = surfacePos.up(1).add(sideDx, 0, sideDz);
        if (world.isAir(sidePos) || world.getBlockState(sidePos).isReplaceable()) {
            world.setBlockState(sidePos, ModBlocks.COCONUT_LOG.getDefaultState(), 3);
        }
        // 反方向撑脚
        BlockPos oppPos = surfacePos.up(1).add(oppDx, 0, oppDz);
        if (world.isAir(oppPos) || world.getBlockState(oppPos).isReplaceable()) {
            world.setBlockState(oppPos, ModBlocks.COCONUT_LOG.getDefaultState(), 3);
        }
    }

    /**
     * 检查地面是否适合棕榈树生长
     */
    private static boolean isValidGround(StructureWorldAccess world, BlockPos pos) {
        return world.getBlockState(pos).isOf(Blocks.SAND)
            || world.getBlockState(pos).isOf(Blocks.RED_SAND)
            || world.getBlockState(pos).isOf(Blocks.GRASS_BLOCK);
    }
}
