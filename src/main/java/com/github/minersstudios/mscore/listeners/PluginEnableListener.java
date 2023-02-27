package com.github.minersstudios.mscore.listeners;

import com.github.minersstudios.mscore.MSListener;
import com.github.minersstudios.mscore.MSPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@MSListener
public class PluginEnableListener implements Listener {

	@EventHandler
	public void onPluginEnable(@NotNull PluginEnableEvent event) {
		Plugin plugin = event.getPlugin();
		if (plugin instanceof MSPlugin msPlugin) {
			System.out.println(msPlugin.getName());
		}
	}
}
