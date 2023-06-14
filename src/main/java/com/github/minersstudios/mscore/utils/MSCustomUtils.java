package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msitems.items.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@SuppressWarnings("unused")
public final class MSCustomUtils {

	@Contract(value = " -> fail")
	private MSCustomUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Gets an {@link ItemStack} of custom item / block / decor
	 *
	 * @param namespacedKeyStr namespaced key string, example - (msitem:example)
	 * @return {@link ItemStack} of custom item / block / decor
	 * @throws NullPointerException if namespaced key is invalid or null
	 * @throws MSCustomNotFoundException if {@link CustomBlockData} is not found
	 */
	@Contract("null -> null")
	public static @Nullable ItemStack getItemStack(@Nullable String namespacedKeyStr) throws MSCustomNotFoundException {
		if (namespacedKeyStr == null) return null;

		String[] strings = namespacedKeyStr.toLowerCase(Locale.ROOT).split(":");
		String namespace = strings[0];
		String key = strings[1];

		return switch (namespace) {
			case "msblock" -> MSBlockUtils.getCustomBlockItem(key);
			case "msdecor" -> MSDecorUtils.getCustomDecorItem(key);
			case "msitem" -> MSItemUtils.getCustomItemItemStack(key);
			default -> null;
		};
	}

	/**
	 * Gets {@link CustomBlockData} or {@link CustomDecorData} or {@link CustomItem} from {@link ItemStack}
	 *
	 * @param itemStack {@link ItemStack}
	 * @return {@link CustomBlockData} or {@link CustomDecorData} or {@link CustomItem}
	 * @throws MSCustomNotFoundException if custom is not found
	 */
	@Contract("null -> null")
	public static @Nullable Object getCustom(@Nullable ItemStack itemStack) throws MSCustomNotFoundException {
		if (itemStack == null) return null;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) return null;
		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		for (NamespacedKey namespacedKey : container.getKeys()) {
			String key = namespacedKey.getKey();

			switch (namespacedKey.namespace()) {
				case "msblock" -> {
					return MSBlockUtils.getCustomBlockData(key);
				}
				case "msdecor" -> {
					return MSDecorUtils.getCustomDecorData(key);
				}
				case "msitem" -> {
					return MSItemUtils.getCustomItem(key);
				}
			}
		}
		return null;
	}

	/**
	 * Gets {@link CustomBlockData} or {@link CustomDecorData} or {@link CustomItem} from namespaced key
	 *
	 * @param namespacedKeyStr namespaced key string, example - (msitem:example)
	 * @return {@link CustomBlockData} or {@link CustomDecorData} or {@link CustomItem}
	 * @throws NullPointerException if namespaced key is invalid or null
	 * @throws MSCustomNotFoundException if custom is not found
	 */
	@Contract("null -> null")
	public static @Nullable Object getCustom(@Nullable String namespacedKeyStr) throws MSCustomNotFoundException {
		if (namespacedKeyStr == null) return null;

		String[] strings = namespacedKeyStr.toLowerCase(Locale.ROOT).split(":");
		String namespace = strings[0];
		String key = strings[1];

		return switch (namespace) {
			case "msblock" -> MSBlockUtils.getCustomBlockData(key);
			case "msdecor" -> MSDecorUtils.getCustomDecorData(key);
			case "msitem" -> MSItemUtils.getCustomItem(key);
			default -> null;
		};
	}
}
