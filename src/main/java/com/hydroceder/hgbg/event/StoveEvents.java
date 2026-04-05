package com.hydroceder.hgbg.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * 灶台相关事件
 * 提供灶台放入锅、放入物品和结束烹饪的事件监听
 */
public class StoveEvents {
    
    /**
     * 灶台放入锅事件
     * 在玩家将锅放入灶台时触发
     * 
     * 事件参数：
     * - world: 世界对象
     * - pos: 灶台位置
     * - player: 触发事件的玩家
     * - pan: 要放入的锅物品
     * 
     * 返回值：
     * - ActionResult.SUCCESS: 取消后续监听器，保留放入操作
     * - ActionResult.FAIL: 取消后续监听器，取消放入操作
     * - ActionResult.PASS: 继续下一个监听器
     */
    public static final Event<PlacePanCallback> PLACE_PAN = EventFactory.createArrayBacked(
        PlacePanCallback.class,
        callbacks -> (world, pos, player, pan) -> {
            for (PlacePanCallback callback : callbacks) {
                ActionResult result = callback.onPlacePan(world, pos, player, pan);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        }
    );
    
    /**
     * 灶台放入物品事件
     * 在玩家将物品放入灶台（已有锅）时触发
     * 
     * 事件参数：
     * - world: 世界对象
     * - pos: 灶台位置
     * - player: 触发事件的玩家
     * - item: 要放入的物品
     * 
     * 返回值：
     * - ActionResult.SUCCESS: 取消后续监听器，保留放入操作
     * - ActionResult.FAIL: 取消后续监听器，取消放入操作
     * - ActionResult.PASS: 继续下一个监听器
     */
    public static final Event<PlaceItemCallback> PLACE_ITEM = EventFactory.createArrayBacked(
        PlaceItemCallback.class,
        callbacks -> (world, pos, player, item) -> {
            for (PlaceItemCallback callback : callbacks) {
                ActionResult result = callback.onPlaceItem(world, pos, player, item);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        }
    );
    
    /**
     * 灶台结束烹饪事件
     * 在灶台烹饪完成时触发
     * 
     * 事件参数：
     * - world: 世界对象
     * - pos: 灶台位置
     * - hasValidRecipe: 此次烹饪是否为有效配方
     * - outputs: 烹饪结果物品列表
     * 
     * 返回值：
     * - ActionResult.SUCCESS: 取消后续监听器，保留烹饪结果
     * - ActionResult.FAIL: 取消后续监听器，取消烹饪结果（不会弹出物品）
     * - ActionResult.PASS: 继续下一个监听器
     */
    public static final Event<FinishCookingCallback> FINISH_COOKING = EventFactory.createArrayBacked(
        FinishCookingCallback.class,
        callbacks -> (world, pos, hasValidRecipe, outputs) -> {
            for (FinishCookingCallback callback : callbacks) {
                ActionResult result = callback.onFinishCooking(world, pos, hasValidRecipe, outputs);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        }
    );
    
    /**
     * 灶台放入锅回调接口
     */
    @FunctionalInterface
    public interface PlacePanCallback {
        ActionResult onPlacePan(World world, BlockPos pos, PlayerEntity player, ItemStack pan);
    }
    
    /**
     * 灶台放入物品回调接口
     */
    @FunctionalInterface
    public interface PlaceItemCallback {
        ActionResult onPlaceItem(World world, BlockPos pos, PlayerEntity player, ItemStack item);
    }
    
    /**
     * 灶台结束烹饪回调接口
     */
    @FunctionalInterface
    public interface FinishCookingCallback {
        ActionResult onFinishCooking(World world, BlockPos pos, boolean hasValidRecipe, java.util.List<ItemStack> outputs);
    }
}
