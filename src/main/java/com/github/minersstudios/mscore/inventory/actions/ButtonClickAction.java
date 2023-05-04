package com.github.minersstudios.mscore.inventory.actions;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.inventory.InventoryButton;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ButtonClickAction {
	void doAction(
			@NotNull Player player,
			@NotNull CustomInventory customInventory,
			@NotNull InventoryButton button
	);
}
