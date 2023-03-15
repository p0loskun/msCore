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
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class MSItemUtils {
	public static final NamespacedKey CUSTOM_ITEM_TYPE_NAMESPACED_KEY = new NamespacedKey(MSItems.getInstance(), "type");
	public static final NamespacedKey CUSTOM_ITEM_RENAMEABLE_NAMESPACED_KEY = new NamespacedKey(MSItems.getInstance(), "renameable");

	@Contract(value = " -> fail")
	private MSItemUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * @param itemStack item
	 * @return True if item is {@link CustomItem} or {@link RenameableItem}
	 */
	@Contract("null -> false")
	public static boolean isCustomItem(@Nullable ItemStack itemStack) {
		return isCustomItem(itemStack, true);
	}

	/**
	 * @param itemStack              item
	 * @param checkForRenameableItem if true, the item stack will be checked if it is a {@link RenameableItem}
	 * @return True if item is {@link CustomItem}
	 */
	@Contract("null, _ -> false")
	public static boolean isCustomItem(@Nullable ItemStack itemStack, boolean checkForRenameableItem) {
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
	 * Gets {@link CustomItem} item stack
	 *
	 * @param namespacedKeyStr {@link CustomItem} namespaced key string, example - (msitem:example)
	 * @return {@link CustomItem} item stack
	 */
	@Contract("null -> null")
	public static @Nullable ItemStack getCustomItemItemStack(@Nullable String namespacedKeyStr) {
		if (namespacedKeyStr == null) return null;
		CustomItem customItem = getCustomItem(namespacedKeyStr);
		return customItem == null ? null : customItem.getItemStack();
	}

	/**
	 * Gets {@link CustomItem} from {@link ItemStack}
	 *
	 * @param itemStack {@link ItemStack}
	 * @return {@link CustomItem}
	 */
	@Contract("null -> null")
	public static @Nullable CustomItem getCustomItem(@Nullable ItemStack itemStack) {
		return isCustomItem(itemStack)
				? getCustomItem(
						"msitem:"
						+ itemStack.getItemMeta()
						.getPersistentDataContainer()
						.get(CUSTOM_ITEM_TYPE_NAMESPACED_KEY, PersistentDataType.STRING)
				)
				: null;
	}

	/**
	 * Gets {@link CustomItem} from namespaced key string
	 *
	 * @param namespacedKeyStr {@link CustomItem} namespaced key string, example - (msitem:example)
	 * @return {@link CustomItem}
	 */
	@Contract("null -> null")
	public static @Nullable CustomItem getCustomItem(@Nullable String namespacedKeyStr) {
		if (namespacedKeyStr == null) return null;
		Pattern pattern = Pattern.compile("msitem:(\\w+)");
		Matcher matcher = pattern.matcher(namespacedKeyStr.toLowerCase(Locale.ROOT));
		if (matcher.find()) {
			return MSCore.getConfigCache().customItemMap.getByPrimaryKey(matcher.group(1));
		}
		return null;
	}
}
