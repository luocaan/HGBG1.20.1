package com.hydroceder.hgbg.block.entity;

import com.hydroceder.hgbg.block.ModBlocks;
import com.hydroceder.hgbg.block.ModBlockEntityTypes;
import com.hydroceder.hgbg.block.MetronomeBlock;
import com.hydroceder.hgbg.music.JukeMusicPlayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Random;

public class CoinOperatedMachineBlockEntity extends BlockEntity {

    private static final Random RANDOM = new Random();

    private long lastEjectTime = -1L;
    private int requiredCoins = 3 + java.util.concurrent.ThreadLocalRandom.current().nextInt(4);
    private static final long COOLDOWN_TICKS = 24000L;
    
    private JukeMusicPlayer musicPlayer = new JukeMusicPlayer();
    
    private static final float[] BPM_SPEED_MULTIPLIERS = {0.5f, 0.7f, 1.0f, 1.4f, 2.0f};
    
    private static final Direction[] NEIGHBOR_DIRECTIONS = {
        Direction.UP,
        Direction.DOWN,
        Direction.NORTH,
        Direction.SOUTH,
        Direction.EAST,
        Direction.WEST
    };

    public CoinOperatedMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COIN_OPERATED_MACHINE_BLOCK_ENTITY, pos, state);
    }

    public boolean canInteract() {
        if (world == null) return false;
        if (lastEjectTime == -1L) return true;

        long currentTime = world.getTimeOfDay();
        if (currentTime >= lastEjectTime) {
            return (currentTime - lastEjectTime) >= COOLDOWN_TICKS;
        } else {
            return (currentTime + 24000L - lastEjectTime) >= COOLDOWN_TICKS;
        }
    }

    public void recordEjectTime() {
        if (world != null) {
            this.lastEjectTime = world.getTimeOfDay();
            this.requiredCoins = 3 + java.util.concurrent.ThreadLocalRandom.current().nextInt(4);
            markDirty();
        }
    }

    public int getRequiredCoins() {
        return requiredCoins;
    }

    public void startMusicPlayback() {
        if (world instanceof ServerWorld) {
            java.util.List<String> musicFiles = JukeMusicPlayer.getAvailableMusicFiles();
            
            if (!musicFiles.isEmpty()) {
                int randomIndex = RANDOM.nextInt(musicFiles.size());
                String randomMusic = musicFiles.get(randomIndex);
                musicPlayer.loadMusicFile(randomMusic);
                
                updatePlaybackSpeedFromMetronome();
                
                markDirty();
                
                com.hydroceder.hgbg.event.CoinMachineMusicTicker.addActiveMachine(this);
            }
        }
    }

    public void stopMusicPlayback() {
        musicPlayer.stopPlaying();
        markDirty();
        
        com.hydroceder.hgbg.event.CoinMachineMusicTicker.removeActiveMachine(this);
    }

    public boolean isPlayingMusic() {
        return musicPlayer.isPlaying();
    }

    public void serverTick() {
        if (world instanceof ServerWorld && musicPlayer.isPlaying()) {
            updatePlaybackSpeedFromMetronome();
            
            ServerWorld serverWorld = (ServerWorld) world;
            musicPlayer.tick(serverWorld, pos);
        }
    }
    
    private void updatePlaybackSpeedFromMetronome() {
        if (world == null || !(world instanceof ServerWorld)) return;
        
        ServerWorld serverWorld = (ServerWorld) world;
        
        int bpmIndex = findAdjacentMetronomeBpm(serverWorld);
        
        if (bpmIndex >= 0 && bpmIndex < BPM_SPEED_MULTIPLIERS.length) {
            float newSpeed = BPM_SPEED_MULTIPLIERS[bpmIndex];
            musicPlayer.setPlaybackSpeed(newSpeed);
        } else {
            musicPlayer.setPlaybackSpeed(1.0f);
        }
    }
    
    private int findAdjacentMetronomeBpm(ServerWorld world) {
        BlockPos machinePos = this.pos;
        int foundBpmIndex = -1;
        
        for (Direction dir : NEIGHBOR_DIRECTIONS) {
            BlockPos neighborPos = machinePos.offset(dir);
            BlockState state = world.getBlockState(neighborPos);
            
            if (state.getBlock() == ModBlocks.METRONOME) {
                foundBpmIndex = state.get(MetronomeBlock.BPM);
                break;
            }
        }
        
        return foundBpmIndex;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("LastEjectTime", lastEjectTime);
        nbt.putInt("RequiredCoins", requiredCoins);
        nbt.putBoolean("IsPlayingMusic", musicPlayer.isPlaying());
        nbt.putString("CurrentMusicName", musicPlayer.getCurrentMusicName());
        nbt.putInt("CurrentTick", musicPlayer.getCurrentTick());
        nbt.putFloat("PlaybackSpeed", musicPlayer.getPlaybackSpeed());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.lastEjectTime = nbt.getLong("LastEjectTime");
        this.requiredCoins = nbt.getInt("RequiredCoins");
        
        boolean wasPlaying = nbt.getBoolean("IsPlayingMusic");
        if (wasPlaying) {
            musicPlayer.stopPlaying();
        }
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
