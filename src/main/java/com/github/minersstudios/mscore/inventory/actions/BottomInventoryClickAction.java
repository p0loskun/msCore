package com.github.minersstudios.mscore.inventory.actions;

import com.github.minersstudios.mscore.inventory.CustomInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface BottomInventoryClickAction {
	void doAction(
			@NotNull Player player,
			@NotNull CustomInventory customInventory,
			@NotNull InventoryAction action,
			@NotNull ClickType clickType,
			@Nullable ItemStack current
	);
}
