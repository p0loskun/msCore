package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.mscore.inventory.ListedInventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@SuppressWarnings("unused")
public final class InventoryUtils {

	@Contract(value = " -> fail")
	private InventoryUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * @param key custom inventory key
	 * @return custom inventory associated with key, or null if there is no custom inventory for the key
	 */
	public static @Nullable CustomInventory getCustomInventory(@NotNull String key) {
		CustomInventory customInventory = MSCore.getConfigCache().customInventories.get(key);
		return customInventory instanceof ListedInventory listedInventory ? listedInventory.getPage(0) : customInventory;
	}

	/**
	 * @param key             custom inventory key
	 * @param customInventory the custom inventory
	 * @return the previous custom inventory associated with key, or null if there was no custom inventory for key
	 */
	public static @Nullable CustomInventory registerCustomInventory(
			@NotNull String key,
			@NotNull CustomInventory customInventory
	) {
		return MSCore.getConfigCache().customInventories.put(key.toLowerCase(Locale.ENGLISH), customInventory);
	}

	/**
	 * @param key custom inventory key
	 * @return the previous custom inventory associated with key, or null if there was no custom inventory for key
	 */
	public static @Nullable CustomInventory unregisterCustomInventory(@NotNull String key) {
		return MSCore.getConfigCache().customInventories.remove(key.toLowerCase(Locale.ENGLISH));
	}
}
