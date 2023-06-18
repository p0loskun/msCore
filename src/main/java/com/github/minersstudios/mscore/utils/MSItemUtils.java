package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.msitems.MSItems;
import com.github.minersstudios.msitems.items.CustomItem;
import com.github.minersstudios.msitems.items.RenameableItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public final class MSItemUtils {
	public static final NamespacedKey CUSTOM_ITEM_TYPE_NAMESPACED_KEY = new NamespacedKey(MSItems.getInstance(), "type");
	public static final NamespacedKey CUSTOM_ITEM_RENAMEABLE_NAMESPACED_KEY = new NamespacedKey(MSItems.getInstance(), "renameable");
	public static final String NAMESPACED_KEY_REGEX = "msitem:(\\w+)";

	@Contract(value = " -> fail")
	private MSItemUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * @param itemStack item
	 * @return True if item is {@link CustomItem}
	 */
	@Contract("null -> false")
	public static boolean isCustomItem(@Nullable ItemStack itemStack) {
		if (itemStack == null) return false;
		ItemMeta itemMeta = itemStack.getItemMeta();
		return itemMeta != null && itemMeta.getPersistentDataContainer().has(CUSTOM_ITEM_TYPE_NAMESPACED_KEY);
	}

	/**
	 * @param itemStack item
	 * @return True if item is {@link RenameableItem}
	 */
	@Contract("null -> false")
	public static boolean isRenameableItem(@Nullable ItemStack itemStack) {
		if (itemStack == null) return false;
		ItemMeta itemMeta = itemStack.getItemMeta();
		return itemMeta != null && itemMeta.getPersistentDataContainer().has(CUSTOM_ITEM_RENAMEABLE_NAMESPACED_KEY);
	}

	/**
	 * Gets {@link CustomItem} item stack from key
	 *
	 * @param key {@link CustomItem} key string
	 * @return {@link CustomItem} item stack
	 */
	public static @NotNull ItemStack getCustomItemItemStack(@NotNull String key) throws MSCustomNotFoundException {
		return getCustomItem(key).getItemStack();
	}

	/**
	 * Gets {@link CustomItem} from {@link ItemStack}
	 *
	 * @param itemStack {@link ItemStack}
	 * @return {@link CustomItem}
	 * @throws MSCustomNotFoundException if {@link CustomItem} is not found
	 */
	@Contract("null -> null")
	public static @Nullable CustomItem getCustomItem(@Nullable ItemStack itemStack) throws MSCustomNotFoundException {
		if (itemStack == null) return null;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) return null;
		String key = itemMeta.getPersistentDataContainer().get(CUSTOM_ITEM_TYPE_NAMESPACED_KEY, PersistentDataType.STRING);
		return key == null ? null : getCustomItem(key);
	}

	/**
	 * Gets {@link CustomItem} from key
	 *
	 * @param key {@link CustomItem} key string
	 * @return {@link CustomItem}
	 * @throws MSCustomNotFoundException if {@link CustomItem} is not found
	 */
	public static @NotNull CustomItem getCustomItem(@NotNull String key) throws MSCustomNotFoundException {
		CustomItem customItem = MSCore.getConfigCache().customItemMap.getByPrimaryKey(key);
		if (customItem == null) {
			throw new MSCustomNotFoundException("Custom item is not found : " + key);
		}
		return customItem;
	}

	@Contract(value = "null -> false", pure = true)
	public static boolean matchesNamespacedKey(@Nullable String string) {
		return string != null && string.matches(NAMESPACED_KEY_REGEX);
	}
}
