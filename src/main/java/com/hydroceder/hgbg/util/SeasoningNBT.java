package com.hydroceder.hgbg.util;

import com.hydroceder.hgbg.seasoning.Seasoning;
import com.hydroceder.hgbg.seasoning.SeasoningRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.List;

/**
 * 调味料NBT工具类
 * 用于处理食物的调味料NBT标记和效果
 * 支持多个调味料
 */
public class SeasoningNBT {
    public static final String SEASONINGS_KEY = "hgbg_seasonings";

    /**
     * 为物品添加一个调味料
     */
    public static ItemStack addSeasoning(ItemStack stack, String seasoningId) {
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList list = nbt.getList(SEASONINGS_KEY, 8);
        
        for (int i = 0; i < list.size(); i++) {
            if (list.getString(i).equals(seasoningId)) {
                return stack;
            }
        }
        
        list.add(NbtString.of(seasoningId));
        nbt.put(SEASONINGS_KEY, list);
        
        return stack;
    }

    /**
     * 为物品添加多个调味料
     */
    public static ItemStack addSeasonings(ItemStack stack, List<String> seasoningIds) {
        for (String seasoningId : seasoningIds) {
            addSeasoning(stack, seasoningId);
        }
        return stack;
    }

    /**
     * 从材料列表中提取所有调味料ID
     */
    public static List<String> extractSeasoningIds(List<ItemStack> materials) {
        List<String> result = new ArrayList<>();
        for (ItemStack stack : materials) {
            Seasoning seasoning = SeasoningRegistry.getSeasoning(stack.getItem());
            if (seasoning != null && !result.contains(seasoning.getId())) {
                result.add(seasoning.getId());
            }
        }
        return result;
    }
    
    /**
     * 分离调味料和食材
     * 返回分离结果：seasoningIds - 调味料ID列表，nonSeasoningMaterials - 非调味料材料列表
     */
    public static class SeparationResult {
        public final List<String> seasoningIds;
        public final List<ItemStack> nonSeasoningMaterials;
        
        public SeparationResult(List<String> seasoningIds, List<ItemStack> nonSeasoningMaterials) {
            this.seasoningIds = seasoningIds;
            this.nonSeasoningMaterials = nonSeasoningMaterials;
        }
    }
    
    /**
     * 从材料列表中分离调味料和食材
     * 调味料会被完全消耗（不计入配方匹配）
     */
    public static SeparationResult separateSeasonings(List<ItemStack> materials) {
        List<String> seasoningIds = new ArrayList<>();
        List<ItemStack> nonSeasoningMaterials = new ArrayList<>();
        
        for (ItemStack stack : materials) {
            Seasoning seasoning = SeasoningRegistry.getSeasoning(stack.getItem());
            if (seasoning != null) {
                if (!seasoningIds.contains(seasoning.getId())) {
                    seasoningIds.add(seasoning.getId());
                }
            } else {
                nonSeasoningMaterials.add(stack.copy());
            }
        }
        
        return new SeparationResult(seasoningIds, nonSeasoningMaterials);
    }

    /**
     * 检查物品是否有调味料
     */
    public static boolean hasSeasonings(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains(SEASONINGS_KEY);
    }

    /**
     * 获取物品的所有调味料
     */
    public static List<String> getSeasoningIds(ItemStack stack) {
        List<String> result = new ArrayList<>();
        if (hasSeasonings(stack)) {
            NbtList list = stack.getNbt().getList(SEASONINGS_KEY, 8);
            for (int i = 0; i < list.size(); i++) {
                result.add(list.getString(i));
            }
        }
        return result;
    }

    /**
     * 获取物品的所有调味料对象
     */
    public static List<Seasoning> getSeasonings(ItemStack stack) {
        List<Seasoning> result = new ArrayList<>();
        for (String id : getSeasoningIds(stack)) {
            Seasoning seasoning = SeasoningRegistry.getSeasoning(id);
            if (seasoning != null) {
                result.add(seasoning);
            }
        }
        return result;
    }

    /**
     * 应用所有调味料效果
     */
    public static void applySeasoningEffects(ItemStack stack, LivingEntity user) {
        for (Seasoning seasoning : getSeasonings(stack)) {
            seasoning.applyEffect(user);
        }
    }
}
