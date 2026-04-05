package com.hydroceder.hgbg.config;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * 模组配置类
 * 用于管理模组的配置选项
 */
public class ModConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("hunger-begone");
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("hunger-begone.properties");
    
    private static boolean sprintNoExhaustion = true;
    private static boolean giveHomelandDirt = true;
    
    /**
     * 加载配置文件
     */
    public static void load() {
        Properties properties = new Properties();
        boolean configUpdated = false;
        
        // 如果配置文件存在，加载它
        if (Files.exists(CONFIG_PATH)) {
            try {
                properties.load(Files.newBufferedReader(CONFIG_PATH));
                
                // 加载sprintNoExhaustion选项
                sprintNoExhaustion = Boolean.parseBoolean(properties.getProperty("sprintNoExhaustion", "true"));
                
                // 加载giveHomelandDirt选项，如果不存在则添加默认值
                if (!properties.containsKey("giveHomelandDirt")) {
                    properties.setProperty("giveHomelandDirt", "true");
                    configUpdated = true;
                }
                giveHomelandDirt = Boolean.parseBoolean(properties.getProperty("giveHomelandDirt"));
                
                LOGGER.info("Loaded config: sprintNoExhaustion={}, giveHomelandDirt={}", sprintNoExhaustion, giveHomelandDirt);
                
                // 如果配置文件被更新，保存它
                if (configUpdated) {
                    try {
                        properties.store(Files.newBufferedWriter(CONFIG_PATH), "Hunger Begone Config");
                        LOGGER.info("Updated config file with missing options");
                    } catch (IOException e) {
                        LOGGER.error("Failed to update config file", e);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Failed to load config file", e);
                // 使用默认值
                sprintNoExhaustion = true;
                giveHomelandDirt = true;
            }
        } else {
            // 配置文件不存在，创建默认配置
            properties.setProperty("sprintNoExhaustion", "true");
            properties.setProperty("giveHomelandDirt", "true");
            try {
                Files.createDirectories(CONFIG_PATH.getParent());
                properties.store(Files.newBufferedWriter(CONFIG_PATH), "Hunger Begone Config");
                LOGGER.info("Created default config file");
            } catch (IOException e) {
                LOGGER.error("Failed to create config file", e);
            }
        }
    }
    
    /**
     * 检查是否开启疾跑不增加疲劳的功能
     * @return true 表示开启，false 表示关闭
     */
    public static boolean isSprintNoExhaustionEnabled() {
        return sprintNoExhaustion;
    }
    
    /**
     * 检查是否开启故乡土壤给予功能
     * @return true 表示开启，false 表示关闭
     */
    public static boolean isGiveHomelandDirtEnabled() {
        return giveHomelandDirt;
    }
}