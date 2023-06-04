package com.github.minersstudios.mscore.listeners.command;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.github.minersstudios.mscore.config.ConfigCache;
import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.CommandUtils;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings({"UnstableApiUsage"})
@MSListener
public final class AsyncPlayerSendCommandsListener implements Listener {

    @EventHandler
    public void onAsyncPlayerSendCommands(@NotNull AsyncPlayerSendCommandsEvent<CommandSourceStack> event) {
        if (event.isAsynchronous() || !event.hasFiredAsync()) {
            Player player = event.getPlayer();
            for (Map.Entry<LiteralCommandNode<CommandSourceStack>, String> entry : ConfigCache.COMMANDS.entrySet()) {
                LiteralCommandNode<CommandSourceStack> literalCommandNode = entry.getKey();
                String permission = entry.getValue();
                if (permission != null && !player.hasPermission(permission)) return;
                RootCommandNode<CommandSourceStack> root = event.getCommandNode();
                CommandUtils.removeCommand(root, literalCommandNode.getName());
                root.addChild(literalCommandNode);
            }
        }
    }
}
