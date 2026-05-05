package com.hydroceder.hgbg.music;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JukeMusicPlayer {

    private final Map<String, List<NoteData>> musicData = new LinkedHashMap<>();
    private int currentTick = 0;
    private boolean isPlaying = false;
    private String currentMusicName = "";
    private int totalTicks = 0;
    private final Random random = new Random();
    
    private float playbackSpeed = 1.0f;
    private float tickAccumulator = 0f;
    private float baseSpeed = 32f;

    public static class NoteData {
        public String instrument;
        public float volume;
        public float pitch;

        public NoteData(String instrument, float volume, float pitch) {
            this.instrument = instrument;
            this.volume = volume;
            this.pitch = pitch;
        }
    }

    public void loadMusicFile(String musicName) {
        try {
            String path = "/data/hunger-begone/juke_music/" + musicName + ".json";
            InputStream is = getClass().getResourceAsStream(path);
            
            if (is == null) {
                System.err.println("Music file not found: " + path);
                return;
            }

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), JsonObject.class);

            musicData.clear();
            JsonObject ticksJson = json.getAsJsonObject("ticks");
            
            if (ticksJson != null) {
                for (Map.Entry<String, JsonElement> entry : ticksJson.entrySet()) {
                    String tickStr = entry.getKey();
                    JsonArray notesArray = entry.getValue().getAsJsonArray();
                    
                    List<NoteData> notes = new ArrayList<>();
                    for (JsonElement noteElement : notesArray) {
                        JsonObject noteObj = noteElement.getAsJsonObject();
                        String instrument = noteObj.get("instrument").getAsString();
                        float volume = noteObj.get("volume").getAsFloat();
                        float pitch = noteObj.get("pitch").getAsFloat();
                        notes.add(new NoteData(instrument, volume, pitch));
                    }
                    
                    musicData.put(tickStr, notes);
                }
            }

            currentTick = 0;
            currentMusicName = musicName;
            totalTicks = calculateTotalTicks();
            
            if (json.has("speed")) {
                baseSpeed = json.get("speed").getAsFloat();
            } else {
                baseSpeed = 32f;
            }
            
            isPlaying = true;
            tickAccumulator = 0f;

            is.close();
        } catch (Exception e) {
            System.err.println("Failed to load music file: " + musicName);
            e.printStackTrace();
            isPlaying = false;
        }
    }

    private int calculateTotalTicks() {
        int maxTick = 0;
        for (String tickStr : musicData.keySet()) {
            int tick = Integer.parseInt(tickStr);
            if (tick > maxTick) {
                maxTick = tick;
            }
        }
        return maxTick + 1;
    }

    public void setPlaybackSpeed(float speed) {
        float clampedSpeed = Math.max(0.1f, Math.min(3.0f, speed));
        if (this.playbackSpeed != clampedSpeed) {
            this.playbackSpeed = clampedSpeed;
        }
    }

    public void tick(ServerWorld world, BlockPos pos) {
        if (!isPlaying || world == null) return;

        float tickRate = (baseSpeed / 20f) * playbackSpeed;
        
        tickAccumulator += tickRate;
        
        while (tickAccumulator >= 1.0f && isPlaying) {
            tickAccumulator -= 1.0f;
            processSingleTick(world, pos);
        }
    }
    
    private void processSingleTick(ServerWorld world, BlockPos pos) {
        if (!isPlaying) return;
        
        String tickKey = String.valueOf(currentTick);
        
        if (musicData.containsKey(tickKey)) {
            playNotesAtTick(world, pos, musicData.get(tickKey));
        }

        currentTick++;

        if (currentTick > totalTicks) {
            stopPlaying();
        }
    }

    private void playNotesAtTick(ServerWorld world, BlockPos pos, List<NoteData> notes) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.8;
        double z = pos.getZ() + 0.5;

        for (NoteData note : notes) {
            SoundEvent soundEvent = getSoundEventForInstrument(note.instrument);
            if (soundEvent != null) {
                world.playSound(null, x, y, z,
                    soundEvent,
                    SoundCategory.BLOCKS,
                    note.volume,
                    note.pitch);
                
                world.emitGameEvent(null, GameEvent.NOTE_BLOCK_PLAY, pos);
            }
        }
        
        if (!notes.isEmpty()) {
            spawnNoteParticle(world, pos);
        }
    }

    private SoundEvent getSoundEventForInstrument(String instrument) {
        switch (instrument.toLowerCase()) {
            case "harp":
                return SoundEvents.BLOCK_NOTE_BLOCK_HARP.value();
            case "guitar":
                return SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value();
            case "chime":
                return SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value();
            case "bass":
                return SoundEvents.BLOCK_NOTE_BLOCK_BASS.value();
            case "hat":
                return SoundEvents.BLOCK_NOTE_BLOCK_HAT.value();
            case "snare":
                return SoundEvents.BLOCK_NOTE_BLOCK_SNARE.value();
            default:
                return SoundEvents.BLOCK_NOTE_BLOCK_HARP.value();
        }
    }

    private void spawnNoteParticle(ServerWorld world, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.9;
        double z = pos.getZ() + 0.5;

        double offsetX = (random.nextDouble() - 0.5) * 0.6;
        double offsetZ = (random.nextDouble() - 0.5) * 0.6;
        
        double particleX = x + offsetX;
        double particleY = y + random.nextDouble() * 0.3;
        double particleZ = z + offsetZ;

        world.spawnParticles(
            ParticleTypes.NOTE,
            particleX, particleY, particleZ,
            1,
            0.0, 0.05, 0.0,
            0.01
        );
    }

    public void stopPlaying() {
        isPlaying = false;
        currentTick = 0;
        musicData.clear();
        currentMusicName = "";
        totalTicks = 0;
        playbackSpeed = 1.0f;
        baseSpeed = 32f;
        tickAccumulator = 0f;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public String getCurrentMusicName() {
        return currentMusicName;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public int getTotalTicks() {
        return totalTicks;
    }
    
    public float getPlaybackSpeed() {
        return playbackSpeed;
    }
    
    public float getBaseSpeed() {
        return baseSpeed;
    }

    public static List<String> getAvailableMusicFiles() {
        List<String> musicFiles = new ArrayList<>();
        
        try {
            String basePath = "/data/hunger-begone/juke_music/";
            Enumeration<java.net.URL> urls = JukeMusicPlayer.class.getClassLoader().getResources(basePath.replace("/", java.io.File.separator));
            
            boolean foundAny = false;
            while (urls.hasMoreElements()) {
                java.net.URL url = urls.nextElement();
                
                if (url.getProtocol().equals("file")) {
                    java.io.File dir = new java.io.File(url.toURI());
                    if (dir.isDirectory()) {
                        java.io.File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
                        if (files != null) {
                            for (java.io.File file : files) {
                                musicFiles.add(file.getName().replace(".json", ""));
                                foundAny = true;
                            }
                        }
                    }
                } else if (url.getProtocol().equals("jar")) {
                    try {
                        String path = url.getPath();
                        String jarPath = path.substring(5, path.indexOf("!"));
                        java.util.jar.JarFile jarFile = new java.util.jar.JarFile(jarPath);
                        java.util.Enumeration<java.util.jar.JarEntry> entries = jarFile.entries();
                        
                        while (entries.hasMoreElements()) {
                            java.util.jar.JarEntry entry = entries.nextElement();
                            String entryName = entry.getName();
                            if (entryName.startsWith("data/hunger-begone/juke_music/") && 
                                entryName.endsWith(".json") && 
                                !entryName.endsWith("/")) {
                                String fileName = entryName.substring(entryName.lastIndexOf('/') + 1);
                                musicFiles.add(fileName.replace(".json", ""));
                                foundAny = true;
                            }
                        }
                        jarFile.close();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
            
            if (!foundAny) {
                musicFiles.addAll(getMusicFilesFromResourceListing());
            }
            
        } catch (Exception e) {
            // ignore
        }
        
        return musicFiles;
    }
    
    private static List<String> getMusicFilesFromResourceListing() {
        List<String> musicFiles = new ArrayList<>();
        
        String[] knownMusicFiles = {
            "tongji_music",
            "kmb_music",
            "olmg_music",
            "touhou_zsjl_music",
            "gzqs_music",
            "mc_c418_haggstrom_music",
            "gbc_cry",
            "gbc_xxll"
        };
        
        for (String fileName : knownMusicFiles) {
            String testPath = "/data/hunger-begone/juke_music/" + fileName + ".json";
            InputStream testStream = JukeMusicPlayer.class.getResourceAsStream(testPath);
            
            if (testStream != null) {
                musicFiles.add(fileName);
                try {
                    testStream.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        
        return musicFiles;
    }
}
