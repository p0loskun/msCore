package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.MSCore;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class MSBlockUtils {

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
		if (itemMeta == null || !itemMeta.hasCustomModelData()) return false;
		CustomBlockData customBlockData = MSCore.getConfigCache().customBlockMap.getByPrimaryKey(itemMeta.getCustomModelData());
		return customBlockData != null
				&& customBlockData.getItemCustomModelData() == itemMeta.getCustomModelData();
	}

	/**
	 * Gets {@link CustomBlockData} item stack
	 *
	 * @param namespacedKeyStr {@link CustomBlockData} namespaced key string, example - (msblock:example)
	 * @return {@link CustomBlockData} item stack
	 */
	public static @Nullable ItemStack getCustomBlockItem(@NotNull String namespacedKeyStr) {
		CustomBlockData customBlockData = getCustomBlockData(namespacedKeyStr);
		return customBlockData == null ? null : customBlockData.craftItemStack();
	}

	/**
	 * Gets {@link CustomBlockData} from namespaced key string
	 *
	 * @param namespacedKeyStr {@link CustomBlockData} namespaced key string, example - (msblock:example)
	 * @return {@link CustomBlockData}
	 */
	public static @Nullable CustomBlockData getCustomBlockData(@NotNull String namespacedKeyStr) {
		Pattern pattern = Pattern.compile("msblock:(\\w+)");
		Matcher matcher = pattern.matcher(namespacedKeyStr.toLowerCase(Locale.ROOT));
		if (matcher.find()) {
			return MSCore.getConfigCache().customBlockMap.getBySecondaryKey(matcher.group(1));
		}
		return null;
	}
}
