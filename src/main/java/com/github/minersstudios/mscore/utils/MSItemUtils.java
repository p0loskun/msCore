package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.msitems.items.CustomItem;
import com.github.minersstudios.msitems.items.RenameableItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class MSItemUtils {

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
		if (itemMeta == null || !itemMeta.hasCustomModelData()) return false;
		CustomItem customItem = MSCore.getConfigCache().customItemMap.getBySecondaryKey(itemMeta.getCustomModelData());
		if (
				customItem != null
				&& customItem.getItemStack().getType() == itemStack.getType()
		) return true;
		return checkForRenameableItem && isRenameableItem(itemStack);
	}

	/**
	 * @param itemStack item
	 * @return True if item is {@link RenameableItem}
	 */
	@Contract("null -> false")
	public static boolean isRenameableItem(@Nullable ItemStack itemStack) {
		if (itemStack == null) return false;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null || !itemMeta.hasCustomModelData()) return false;
		RenameableItem renameableItem = MSCore.getConfigCache().renameableItemMap.getBySecondaryKey(itemMeta.getCustomModelData());
		return renameableItem != null
				&& renameableItem.getResultItemStack().getType() == itemStack.getType();
	}

	/**
	 * @param namespacedKeyStr {@link CustomItem} namespaced key string, example - (msitem:example)
	 * @return {@link CustomItem} item stack
	 */
	public static @Nullable ItemStack getCustomItemItemStack(@NotNull String namespacedKeyStr) {
		Pattern pattern = Pattern.compile("msitem:(\\w+)");
		Matcher matcher = pattern.matcher(namespacedKeyStr.toLowerCase(Locale.ROOT));
		if (matcher.find()) {
			CustomItem customItem = MSCore.getConfigCache().customItemMap.getByPrimaryKey(matcher.group(1));
			if (customItem == null) return null;
			return customItem.getItemStack();
		}
		return null;
	}
}
