package com.hydroceder.hgbg.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模组音效注册类
 */
public class ModSounds {
    private static final Logger LOGGER = LoggerFactory.getLogger("hunger-begone");
    
    // 敲击音效事件（包含hit和hit2两个子音效，由音效系统自动随机选择）
    public static final SoundEvent HIT = SoundEvent.of(new Identifier("hunger-begone", "hit"));
    
    // 烤箱叮声音效事件
    public static final SoundEvent BELL = SoundEvent.of(new Identifier("hunger-begone", "bell"));
    
    // 烤箱工作音效事件
    public static final SoundEvent OVEN_WORKING = SoundEvent.of(new Identifier("hunger-begone", "oven_working"));
    
    // The_Newage 唱片音效事件
    public static final SoundEvent MUSIC_DISC_THE_NEWAGE = SoundEvent.of(new Identifier("hunger-begone", "music_disc.the_newage"));
    
    // Afternoon 唱片音效事件
    public static final SoundEvent MUSIC_DISC_AFTERNOON = SoundEvent.of(new Identifier("hunger-begone", "music_disc.afternoon"));
    
    // 杀虫剂启动音效事件
    public static final SoundEvent INSECTICIDE_START = SoundEvent.of(new Identifier("hunger-begone", "insecticide_start"));
    
    // 杀虫剂使用音效事件
    public static final SoundEvent INSECTICIDE_USE = SoundEvent.of(new Identifier("hunger-begone", "insecticide_use"));
    
    /**
     * 注册所有音效
     */
    public static void register() {
        Registry.register(Registries.SOUND_EVENT, new Identifier("hunger-begone", "hit"), HIT);
        Registry.register(Registries.SOUND_EVENT, new Identifier("hunger-begone", "bell"), BELL);
        Registry.register(Registries.SOUND_EVENT, new Identifier("hunger-begone", "oven_working"), OVEN_WORKING);
        Registry.register(Registries.SOUND_EVENT, new Identifier("hunger-begone", "music_disc.the_newage"), MUSIC_DISC_THE_NEWAGE);
        Registry.register(Registries.SOUND_EVENT, new Identifier("hunger-begone", "music_disc.afternoon"), MUSIC_DISC_AFTERNOON);
        Registry.register(Registries.SOUND_EVENT, new Identifier("hunger-begone", "insecticide_start"), INSECTICIDE_START);
        Registry.register(Registries.SOUND_EVENT, new Identifier("hunger-begone", "insecticide_use"), INSECTICIDE_USE);
        LOGGER.info("Mod sounds registered successfully!");
    }
}