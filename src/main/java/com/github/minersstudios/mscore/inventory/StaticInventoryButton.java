package com.github.minersstudios.mscore.inventory;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StaticInventoryButton {
    @NotNull InventoryButton getButton(@NotNull ListedInventory listedInventory);
}
