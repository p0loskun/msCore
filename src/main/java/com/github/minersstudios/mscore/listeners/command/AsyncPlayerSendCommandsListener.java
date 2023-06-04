package com.github.minersstudios.mscore.listeners.command;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.github.minersstudios.mscore.config.ConfigCache;
import com.github.minersstudios.mscore.listener.MSListener;
import com.github.minersstudios.mscore.utils.CommandUtils;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "rawtypes", "unchecked"})
@MSListener
public final class AsyncPlayerSendCommandsListener implements Listener {

    @EventHandler
    public void onAsyncPlayerSendCommands(@NotNull AsyncPlayerSendCommandsEvent<?> event) {
        if (event.isAsynchronous() || !event.hasFiredAsync()) {
            Player player = event.getPlayer();
            for (var entry : ConfigCache.COMMANDS.entrySet()) {
                CommandNode<?> commandNode = entry.getKey();
                String permission = entry.getValue();
                if (permission != null && !player.hasPermission(permission)) return;
                RootCommandNode<?> root = event.getCommandNode();
                CommandUtils.removeCommand(root, commandNode.getName());
                root.addChild((CommandNode) commandNode);
            }
        }
    }
}
