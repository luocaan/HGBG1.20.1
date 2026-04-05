package com.hydroceder.hgbg.command;

import com.hydroceder.hgbg.DebugManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * HGBG模组命令注册类
 */
public class HgbgCommand {
    
    /**
     * 注册所有HGBG命令
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("hgbg")
            .requires(source -> source.hasPermissionLevel(0)) // 所有玩家都可以使用
            .then(CommandManager.literal("debug")
                .then(CommandManager.literal("on")
                    .executes(HgbgCommand::enableDebug))
                .then(CommandManager.literal("off")
                    .executes(HgbgCommand::disableDebug))
            )
        );
    }
    
    /**
     * 启用调试模式
     */
    private static int enableDebug(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        
        if (player != null) {
            DebugManager.enableDebug(player);
            source.sendFeedback(() -> Text.literal("§6[HGBG] §a调试模式已启用"), true);
            return 1;
        }
        
        source.sendError(Text.literal("只有玩家可以使用此命令"));
        return 0;
    }
    
    /**
     * 禁用调试模式
     */
    private static int disableDebug(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        
        if (player != null) {
            DebugManager.disableDebug(player);
            source.sendFeedback(() -> Text.literal("§6[HGBG] §c调试模式已禁用"), true);
            return 1;
        }
        
        source.sendError(Text.literal("只有玩家可以使用此命令"));
        return 0;
    }
}
