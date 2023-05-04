package com.github.minersstudios.mscore.listeners;

import com.github.minersstudios.mscore.MSListener;
import com.github.minersstudios.mscore.inventory.CustomInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryCloseListener implements Listener {

	@EventHandler
	public void onInventoryClose(@NotNull InventoryCloseEvent event) {
		if (event.getInventory() instanceof CustomInventory customInventory) {
			customInventory.doCloseAction((Player) event.getPlayer());
		}
	}
}
