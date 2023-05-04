package com.github.minersstudios.mscore.listeners;

import com.github.minersstudios.mscore.MSListener;
import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.inventory.InventoryButton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@MSListener
public class InventoryClickListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(@NotNull InventoryClickEvent event) {
		Inventory clickedInventory = event.getClickedInventory();
		Inventory topInventory = event.getView().getTopInventory();
		Player player = (Player) event.getWhoClicked();
		ItemStack currentItem = event.getCurrentItem();
		ItemStack cursorItem = event.getCursor();
		InventoryAction action = event.getAction();
		ClickType clickType = event.getClick();

		if (
				clickedInventory == null
				|| !(topInventory instanceof CustomInventory customInventory)
		) return;

		if (
				clickedInventory.getType() == InventoryType.PLAYER
				&& (clickType.isShiftClick() || clickType == ClickType.DOUBLE_CLICK)
		) {
			event.setCancelled(true);
		}

		if (clickedInventory instanceof CustomInventory) {
			InventoryButton inventoryButton = customInventory.getClickedButton(event.getSlot());
			if (inventoryButton != null) {
				inventoryButton.doAction(player, customInventory);
			}
			customInventory.doClickAction(player, action, clickType, currentItem, cursorItem);
			event.setCancelled(!clickType.isCreativeAction());
		} else if (clickedInventory instanceof PlayerInventory) {
			customInventory.doBottomClickAction(player, action, clickType, currentItem);
		}
	}
}
