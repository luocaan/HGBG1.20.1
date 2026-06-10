package com.hydroceder.hgbg.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * 炖锅相关事件
 * 提供炖锅存入物品与输出物品的事件监听
 */
public class StewPotEvents {

    /**
     * 炖锅存入物品事件
     * 在物品被存入炖锅时触发（包括玩家右键放入与吸取掉落物两种方式）
     *
     * 事件参数：
     * - world: 世界对象
     * - pos: 炖锅位置
     * - player: 触发事件的玩家（吸取掉落物时可能为 null）
     * - item: 要存入的物品
     *
     * 返回值：
     * - ActionResult.SUCCESS: 取消后续监听器，保留存入操作
     * - ActionResult.FAIL: 取消后续监听器，取消存入操作
     * - ActionResult.PASS: 继续下一个监听器
     */
    public static final Event<InputItemCallback> INPUT_ITEM = EventFactory.createArrayBacked(
        InputItemCallback.class,
        callbacks -> (world, pos, player, item) -> {
            for (InputItemCallback callback : callbacks) {
                ActionResult result = callback.onInputItem(world, pos, player, item);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        }
    );

    /**
     * 炖锅输出物品事件
     * 在物品从炖锅中输出时触发（包括取出材料、烹饪完成产出、方块被破坏掉落等场景）
     *
     * 事件参数：
     * - world: 世界对象
     * - pos: 炖锅位置
     * - player: 触发事件的玩家（方块被破坏或烹饪产出时可能为 null）
     * - item: 即将输出的物品
     *
     * 返回值：
     * - ActionResult.SUCCESS: 取消后续监听器，保留输出操作
     * - ActionResult.FAIL: 取消后续监听器，取消该物品的输出
     * - ActionResult.PASS: 继续下一个监听器
     */
    public static final Event<OutputItemCallback> OUTPUT_ITEM = EventFactory.createArrayBacked(
        OutputItemCallback.class,
        callbacks -> (world, pos, player, item) -> {
            for (OutputItemCallback callback : callbacks) {
                ActionResult result = callback.onOutputItem(world, pos, player, item);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        }
    );

    /**
     * 炖锅存入物品回调接口
     */
    @FunctionalInterface
    public interface InputItemCallback {
        ActionResult onInputItem(World world, BlockPos pos, PlayerEntity player, ItemStack item);
    }

    /**
     * 炖锅输出物品回调接口
     */
    @FunctionalInterface
    public interface OutputItemCallback {
        ActionResult onOutputItem(World world, BlockPos pos, PlayerEntity player, ItemStack item);
    }
}
