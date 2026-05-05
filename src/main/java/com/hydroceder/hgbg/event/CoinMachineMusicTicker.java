package com.hydroceder.hgbg.event;

import com.hydroceder.hgbg.block.entity.CoinOperatedMachineBlockEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import java.util.concurrent.CopyOnWriteArrayList;

public class CoinMachineMusicTicker {

    private static final CopyOnWriteArrayList<CoinOperatedMachineBlockEntity> activeMachines = new CopyOnWriteArrayList<>();

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register((ServerWorld world) -> {
            tickCoinMachines(world);
        });
    }

    public static void addActiveMachine(CoinOperatedMachineBlockEntity machine) {
        if (!activeMachines.contains(machine)) {
            activeMachines.add(machine);
        }
    }

    public static void removeActiveMachine(CoinOperatedMachineBlockEntity machine) {
        activeMachines.remove(machine);
    }

    private static void tickCoinMachines(ServerWorld world) {
        for (CoinOperatedMachineBlockEntity machine : activeMachines) {
            if (machine.isRemoved() || !machine.isPlayingMusic()) {
                removeActiveMachine(machine);
                continue;
            }
            
            if (machine.getWorld() == world) {
                try {
                    machine.serverTick();
                } catch (Exception e) {
                    removeActiveMachine(machine);
                }
            }
        }
    }
    
    public static int getActiveMachineCount() {
        return activeMachines.size();
    }
}
