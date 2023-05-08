package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.inventory.ListedInventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public final class InventoryUtils {

	@Contract(value = " -> fail")
	private InventoryUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static @Nullable CustomInventory getCustomInventory(@NotNull String name) {
		CustomInventory customInventory = MSCore.getConfigCache().customInventories.get(name);
		return customInventory instanceof ListedInventory listedInventory ? listedInventory.getPage(0) : customInventory;
	}
}
