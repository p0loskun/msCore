package com.github.minersstudios.mscore.inventory.actions;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.inventory.InventoryButton;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ButtonClickAction {

	/**
	 * @param event           the event that triggered the action
	 * @param customInventory custom inventory that is involved in this event
	 * @param button          the button that was clicked
	 */
	void doAction(
			@NotNull InventoryClickEvent event,
			@NotNull CustomInventory customInventory,
			@NotNull InventoryButton button
	);
}
