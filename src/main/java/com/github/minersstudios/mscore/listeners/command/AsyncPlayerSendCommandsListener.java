package com.github.minersstudios.mscore.listeners.command;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.github.minersstudios.mscore.config.ConfigCache;
import com.github.minersstudios.mscore.listener.MSListener;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings({"UnstableApiUsage", "rawtypes", "unchecked"})
@MSListener
public final class AsyncPlayerSendCommandsListener implements Listener {

    @EventHandler
    public void onAsyncPlayerSendCommands(@NotNull AsyncPlayerSendCommandsEvent<?> event) {
        if (event.isAsynchronous() || !event.hasFiredAsync()) {
            Player player = event.getPlayer();
            for (var command : ConfigCache.COMMANDS.entrySet()) {
                LiteralCommandNode<?> literalCommandNode = command.getKey();
                String permission = command.getValue();
                if (permission != null && !player.hasPermission(permission)) return;
                RootCommandNode<?> root = event.getCommandNode();
                try {
                    Method method = RootCommandNode.class.getDeclaredMethod("removeCommand");
                    method.setAccessible(true);
                    method.invoke(root, literalCommandNode.getName());
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                root.addChild((CommandNode) literalCommandNode);
            }
        }
    }
}
