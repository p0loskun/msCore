package com.github.minersstudios.mscore.inventory.actions;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface InventoryCloseAction {
	void doAction(
			@NotNull Player player,
			@NotNull CustomInventory customInventory
	);
}
