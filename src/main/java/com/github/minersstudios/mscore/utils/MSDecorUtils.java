package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class MSDecorUtils {
	public static final ImmutableSet<Material> CUSTOM_DECOR_MATERIALS = Sets.immutableEnumSet(
			Material.BARRIER,
			Material.STRUCTURE_VOID,
			Material.LIGHT
	);

	private MSDecorUtils() {
		throw new IllegalStateException("Utility class");
	}

	@Contract("null -> false")
	public static boolean isCustomDecorMaterial(@Nullable Material material) {
		return CUSTOM_DECOR_MATERIALS.contains(material);
	}

	@Contract("null -> false")
	public static boolean isCustomDecorEntity(@Nullable Entity entity) {
		if (entity == null) return false;
		return entity.getScoreboardTags().contains("customDecor");
	}

	/**
	 * @param itemStack item
	 * @return True if item is {@link CustomDecorData}
	 */
	@Contract("null -> false")
	public static boolean isCustomDecor(@Nullable ItemStack itemStack) {
		if (itemStack == null) return false;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null || !itemMeta.hasCustomModelData()) return false;
		CustomDecorData customDecorData = MSCore.getConfigCache().customDecorMap.getByPrimaryKey(itemMeta.getCustomModelData());
		return customDecorData != null
				&& customDecorData.isSimilar(itemStack);
	}

	/**
	 * @param namespacedKeyStr {@link CustomDecorData} namespaced key string, example - (msdecor:example)
	 * @return {@link CustomDecorData} item stack
	 */
	public static @Nullable ItemStack getCustomDecorItem(@NotNull String namespacedKeyStr) {
		Pattern pattern = Pattern.compile("msdecor:\\w+");
		Matcher matcher = pattern.matcher(namespacedKeyStr.toLowerCase(Locale.ROOT));
		if (matcher.find()) {
			CustomDecorData customDecorData = MSCore.getConfigCache().customDecorMap.getBySecondaryKey(matcher.group(1));
			if (customDecorData == null) return null;
			return customDecorData.getItemStack();
		}
		return null;
	}
}
