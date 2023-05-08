package com.github.minersstudios.mscore.inventory;

@FunctionalInterface
public interface StaticInventoryButton {
	InventoryButton getButton(ListedInventory listedInventory);
}
