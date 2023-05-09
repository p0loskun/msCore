package com.github.minersstudios.mscore.inventory.actions;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface InventoryAction<E extends Event> {

	/**
	 * @param event           the event that triggered the action
	 * @param customInventory custom inventory that is involved in this event
	 */
	void doAction(
			@NotNull E event,
			@NotNull CustomInventory customInventory
	);
}
