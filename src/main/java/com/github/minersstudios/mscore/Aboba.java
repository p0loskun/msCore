package com.github.minersstudios.mscore;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class Aboba implements Listener {

	@EventHandler
	public void onAboba(@NotNull PlayerJoinEvent event) {
		System.out.println("a");
	}
}
