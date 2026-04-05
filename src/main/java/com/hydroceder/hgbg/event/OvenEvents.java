package com.hydroceder.hgbg.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;

/**
 * 烤箱相关事件
 * 提供烤箱开关状态变化的事件监听
 */
public class OvenEvents {
    
    /**
     * 烤箱门打开事件
     * 在烤箱门从关闭状态变为打开状态时触发
     * 
     * 事件参数：
     * - world: 世界对象
     * - pos: 烤箱位置
     * - player: 触发事件的玩家（可能为null，如果是通过其他方式开门）
     * 
     * 返回值：
     * - ActionResult.SUCCESS: 取消后续监听器，保留开门操作
     * - ActionResult.FAIL: 取消后续监听器，取消开门操作
     * - ActionResult.PASS: 继续下一个监听器
     */
    public static final Event<OvenDoorCallback> OPEN = EventFactory.createArrayBacked(
        OvenDoorCallback.class,
        callbacks -> (world, pos, player) -> {
            for (OvenDoorCallback callback : callbacks) {
                ActionResult result = callback.onOvenDoor(world, pos, player);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        }
    );
    
    /**
     * 烤箱门关闭事件
     * 在烤箱门从打开状态变为关闭状态时触发
     * 
     * 事件参数：
     * - world: 世界对象
     * - pos: 烤箱位置
     * - player: 触发事件的玩家（可能为null，如果是通过其他方式关门）
     * 
     * 返回值：
     * - ActionResult.SUCCESS: 取消后续监听器，保留关门操作
     * - ActionResult.FAIL: 取消后续监听器，取消关门操作
     * - ActionResult.PASS: 继续下一个监听器
     */
    public static final Event<OvenDoorCallback> CLOSE = EventFactory.createArrayBacked(
        OvenDoorCallback.class,
        callbacks -> (world, pos, player) -> {
            for (OvenDoorCallback callback : callbacks) {
                ActionResult result = callback.onOvenDoor(world, pos, player);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        }
    );
    
    /**
     * 烤箱门回调接口
     */
    @FunctionalInterface
    public interface OvenDoorCallback {
        ActionResult onOvenDoor(World world, BlockPos pos, PlayerEntity player);
    }
}
