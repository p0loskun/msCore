package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.msdecor.MSDecor;
import com.github.minersstudios.msdecor.customdecor.CustomDecor;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class MSDecorUtils {
	public static final NamespacedKey CUSTOM_DECOR_TYPE_NAMESPACED_KEY = new NamespacedKey(MSDecor.getInstance(), "type");
	public static final ImmutableSet<Material> CUSTOM_DECOR_MATERIALS = Sets.immutableEnumSet(
			Material.BARRIER,
			Material.STRUCTURE_VOID,
			Material.LIGHT
	);

	private MSDecorUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static void placeCustomDecor(
			@NotNull Block block,
			@NotNull Player player,
			@NotNull String key,
			@NotNull BlockFace blockFace
	) {
		placeCustomDecor(block, player, key, blockFace, null, null);
	}

	public static void placeCustomDecor(
			@NotNull Block block,
			@NotNull Player player,
			@NotNull String key,
			@NotNull BlockFace blockFace,
			@Nullable EquipmentSlot hand
	) {
		placeCustomDecor(block, player, key, blockFace, hand, null);
	}

	public static void placeCustomDecor(
			@NotNull Block block,
			@NotNull Player player,
			@NotNull String key,
			@NotNull BlockFace blockFace,
			@Nullable EquipmentSlot hand,
			@Nullable Component customName
	) {
		CustomDecorData customDecorData = MSDecorUtils.getCustomDecorData(key);
		if (customDecorData == null) throw new NullPointerException();
		CustomDecor customDecor = new CustomDecor(block, player, customDecorData);
		customDecor.setCustomDecor(blockFace, hand, customName);
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
		return itemMeta != null && itemMeta.getPersistentDataContainer().has(CUSTOM_DECOR_TYPE_NAMESPACED_KEY);
	}

	/**
	 * Gets {@link CustomDecorData} item stack
	 *
	 * @param namespacedKeyStr {@link CustomDecorData} namespaced key string, example - (msdecor:example)
	 * @return {@link CustomDecorData} item stack
	 */
	public static @Nullable ItemStack getCustomDecorItem(@Nullable String namespacedKeyStr) {
		CustomDecorData customDecorData = getCustomDecorData(namespacedKeyStr);
		return customDecorData != null ? customDecorData.getItemStack() : null;
	}

	/**
	 * Gets {@link CustomDecorData} from {@link ItemStack}
	 *
	 * @param itemStack {@link ItemStack}
	 * @return {@link CustomDecorData}
	 */
	public static @Nullable CustomDecorData getCustomDecorData(@Nullable ItemStack itemStack) {
		return isCustomDecor(itemStack)
				? getCustomDecorData(
						"msdecor:"
						+ itemStack.getItemMeta()
						.getPersistentDataContainer()
						.get(CUSTOM_DECOR_TYPE_NAMESPACED_KEY, PersistentDataType.STRING)
				)
				: null;
	}

	/**
	 * Gets {@link CustomDecorData} from namespaced key string
	 *
	 * @param namespacedKeyStr {@link CustomDecorData} namespaced key string, example - (msdecor:example)
	 * @return {@link CustomDecorData}
	 */
	@Contract("null -> null")
	public static @Nullable CustomDecorData getCustomDecorData(@Nullable String namespacedKeyStr) {
		if (namespacedKeyStr == null) return null;
		Pattern pattern = Pattern.compile("msdecor:(\\w+)");
		Matcher matcher = pattern.matcher(namespacedKeyStr.toLowerCase(Locale.ROOT));
		if (matcher.find()) {
			return MSCore.getConfigCache().customDecorMap.getByPrimaryKey(matcher.group(1));
		}
		return null;
	}
}
