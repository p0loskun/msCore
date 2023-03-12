package com.github.minersstudios.mscore.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public final class ItemUtils {

	@Contract(value = " -> fail")
	private ItemUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Gets an {@link ItemStack} of custom item/block/decor
	 *
	 * @param namespacedKeyStr namespaced key string, example - (msitem:example)
	 * @return {@link ItemStack} of custom item/block/decor
	 * @throws NullPointerException if it can't find custom item/block/decor with this namespaced key
	 */
	@Contract("null -> null")
	public static @Nullable ItemStack getMSItemStack(@Nullable String namespacedKeyStr) {
		if (namespacedKeyStr == null) return null;
		if (namespacedKeyStr.matches("msitem:\\w+")) {
			return MSItemUtils.getCustomItemItemStack(namespacedKeyStr);
		} else if (namespacedKeyStr.matches("msblock:\\w+")) {
			return MSBlockUtils.getCustomBlockItem(namespacedKeyStr);
		} else if (namespacedKeyStr.matches("msdecor:\\w+")) {
			return MSDecorUtils.getCustomDecorItem(namespacedKeyStr);
		}
		return null;
	}

	@Contract("null, null -> false")
	public static boolean isSimilarItemStacks(@Nullable ItemStack first, @Nullable ItemStack second) {
		if (
				first == null
				|| second == null
				|| first.getType() != second.getType()
		) return false;
		ItemMeta firstMeta = first.getItemMeta();
		ItemMeta secondMeta = second.getItemMeta();
		if (
				!firstMeta.hasCustomModelData()
				|| !secondMeta.hasCustomModelData()
		) return false;
		return firstMeta.getCustomModelData() == secondMeta.getCustomModelData();
	}

	@Contract("_, null -> false")
	public static boolean isListContainsItem(@NotNull List<ItemStack> list, @Nullable ItemStack item) {
		if (list.isEmpty() || item == null) return false;
		for (ItemStack listItem : list) {
			if (isSimilarItemStacks(listItem, item)) return true;
		}
		return false;
	}
}
