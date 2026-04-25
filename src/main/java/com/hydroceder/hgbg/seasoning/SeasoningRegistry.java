package com.hydroceder.hgbg.seasoning;

import net.minecraft.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 调味料注册表
 * 用于管理所有注册的调味料
 */
public class SeasoningRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger("SeasoningRegistry");
    private static final Map<String, Seasoning> SEASONINGS_BY_ID = new HashMap<>();
    private static final Map<Item, Seasoning> SEASONINGS_BY_ITEM = new HashMap<>();

    /**
     * 注册一个调味料
     */
    public static void register(Seasoning seasoning) {
        if (SEASONINGS_BY_ID.containsKey(seasoning.getId())) {
            LOGGER.warn("Seasoning with id '{}' is already registered! Overwriting...", seasoning.getId());
        }
        SEASONINGS_BY_ID.put(seasoning.getId(), seasoning);
        SEASONINGS_BY_ITEM.put(seasoning.getItem(), seasoning);
        LOGGER.info("Registered seasoning: {}", seasoning.getId());
    }

    /**
     * 根据ID获取调味料
     */
    public static Seasoning getSeasoning(String id) {
        return SEASONINGS_BY_ID.get(id);
    }

    /**
     * 根据物品获取调味料
     */
    public static Seasoning getSeasoning(Item item) {
        return SEASONINGS_BY_ITEM.get(item);
    }

    /**
     * 检查物品是否是调味料
     */
    public static boolean isSeasoning(Item item) {
        return SEASONINGS_BY_ITEM.containsKey(item);
    }

    /**
     * 获取所有已注册的调味料
     */
    public static Collection<Seasoning> getAll() {
        return SEASONINGS_BY_ID.values();
    }

    /**
     * 获取已注册调味料的数量
     */
    public static int size() {
        return SEASONINGS_BY_ID.size();
    }
}
