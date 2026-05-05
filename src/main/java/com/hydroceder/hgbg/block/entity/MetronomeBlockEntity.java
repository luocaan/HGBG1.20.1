package com.hydroceder.hgbg.block.entity;

import com.hydroceder.hgbg.block.MetronomeBlock;
import com.hydroceder.hgbg.block.ModBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MetronomeBlockEntity extends BlockEntity {
    private int tickCounter = 0;

    private static final int BASE_TICKS_PER_LEVEL = 20;
    private static final int BASE_BPM = 60;
    private static final int MAX_POWER = 15;

    public static final int[] BPM_LEVELS = {60, 90, 115, 142, 150};

    public MetronomeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.METRONOME_BLOCK_ENTITY, pos, state);
    }

    public int getCurrentBPM() {
        return BPM_LEVELS[getCachedState().get(MetronomeBlock.BPM)];
    }

    public float getSwingPeriod() {
        float bpm = getCurrentBPM();
        return (2400.0f / bpm);
    }

    public int getTicksPerLevel() {
        float bpm = getCurrentBPM();
        if (bpm <= 0) return BASE_TICKS_PER_LEVEL;
        int ticksPerLevel = (int)(BASE_TICKS_PER_LEVEL * ((float) BASE_BPM / bpm));
        return Math.max(1, ticksPerLevel);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MetronomeBlockEntity entity) {
        if (world.isClient()) return;

        int ticksPerLevel = entity.getTicksPerLevel();
        entity.tickCounter++;
        int newLevel = (entity.tickCounter / ticksPerLevel) % MAX_POWER + 1;
        int currentPower = state.get(MetronomeBlock.POWER);

        if (newLevel != currentPower) {
            world.setBlockState(pos, state.with(MetronomeBlock.POWER, newLevel));
            entity.markDirty();
            world.updateNeighborsAlways(pos, state.getBlock());
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("TickCounter", tickCounter);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        tickCounter = nbt.getInt("TickCounter");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
