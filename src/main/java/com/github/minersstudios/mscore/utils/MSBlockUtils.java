package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.msblock.MSBlock;
import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.MSCore;
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
public final class MSBlockUtils {
	public static final NamespacedKey CUSTOM_BLOCK_TYPE_NAMESPACED_KEY = new NamespacedKey(MSBlock.getInstance(), "type");

	private MSBlockUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * @param itemStack item
	 * @return True if item is {@link CustomBlockData}
	 */
	@Contract("null -> false")
	public static boolean isCustomBlock(@Nullable ItemStack itemStack) {
		if (itemStack == null) return false;
		ItemMeta itemMeta = itemStack.getItemMeta();
		return itemMeta != null && itemMeta.getPersistentDataContainer().has(CUSTOM_BLOCK_TYPE_NAMESPACED_KEY);
	}

	/**
	 * Gets {@link CustomBlockData} item stack
	 *
	 * @param namespacedKeyStr {@link CustomBlockData} namespaced key string, example - (msblock:example)
	 * @return {@link CustomBlockData} item stack
	 */
	public static @Nullable ItemStack getCustomBlockItem(@Nullable String namespacedKeyStr) {
		CustomBlockData customBlockData = getCustomBlockData(namespacedKeyStr);
		return customBlockData == null ? null : customBlockData.craftItemStack();
	}

	/**
	 * Gets {@link CustomBlockData} from {@link ItemStack}
	 *
	 * @param itemStack {@link ItemStack}
	 * @return {@link CustomBlockData}
	 */
	public static @Nullable CustomBlockData getCustomBlockData(@Nullable ItemStack itemStack) {
		return isCustomBlock(itemStack)
				? getCustomBlockData(
						"msblock:"
						+ itemStack.getItemMeta()
						.getPersistentDataContainer()
						.get(CUSTOM_BLOCK_TYPE_NAMESPACED_KEY, PersistentDataType.STRING)
				)
				: null;
	}

	/**
	 * Gets {@link CustomBlockData} from namespaced key string
	 *
	 * @param namespacedKeyStr {@link CustomBlockData} namespaced key string, example - (msblock:example)
	 * @return {@link CustomBlockData}
	 */
	public static @Nullable CustomBlockData getCustomBlockData(@Nullable String namespacedKeyStr) {
		if (namespacedKeyStr == null) return null;
		Pattern pattern = Pattern.compile("msblock:(\\w+)");
		Matcher matcher = pattern.matcher(namespacedKeyStr.toLowerCase(Locale.ROOT));
		if (matcher.find()) {
			return MSCore.getConfigCache().customBlockMap.getByPrimaryKey(matcher.group(1));
		}
		return null;
	}
}
