package com.github.minersstudios.mscore.config;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.msblock.utils.AdaptationUtils;
import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.collections.DualMap;
import com.github.minersstudios.mscore.utils.ItemUtils;
import com.github.minersstudios.mscore.utils.MSBlockUtils;
import com.github.minersstudios.mscore.utils.MSDecorUtils;
import com.github.minersstudios.mscore.utils.MSItemUtils;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msitems.items.CustomItem;
import com.github.minersstudios.msitems.items.RenameableItem;
import com.google.common.collect.Multimap;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftMetaBlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class ConfigCache {
	public final File dataFile;
	public final YamlConfiguration yamlConfiguration;

	public final @NotNull DateTimeFormatter timeFormatter;
	public final boolean updateItemsNBT;

	public final DualMap<String, Integer, CustomDecorData> customDecorMap = new DualMap<>();
	public final List<Recipe> customDecorRecipes = new ArrayList<>();

	public final DualMap<String, Integer, CustomBlockData> customBlockMap = new DualMap<>();
	public final Map<Integer, CustomBlockData> cachedNoteBlockData = new HashMap<>();
	public final List<Recipe> customBlockRecipes = new ArrayList<>();

	public final DualMap<String, Integer, CustomItem> customItemMap = new DualMap<>();
	public final DualMap<String, Integer, RenameableItem> renameableItemMap = new DualMap<>();
	public final List<RenameableItem> renameableItemsMenu = new ArrayList<>();
	public final List<Recipe> customItemRecipes = new ArrayList<>();

	public ConfigCache() {
		this.dataFile = new File(MSCore.getInstance().getPluginFolder(), "config.yml");
		this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.dataFile);

		this.timeFormatter = DateTimeFormatter.ofPattern(this.yamlConfiguration.getString("date-format", "EEE, yyyy-MM-dd HH:mm z"));
		this.updateItemsNBT = this.yamlConfiguration.getBoolean("update-items-nbt");
	}

	public void updateCustomDecors(@NotNull World world) {
		Path path = Paths.get(world.getWorldFolder().getAbsolutePath() + "/entities");
		if (!path.toFile().exists()) return;
		try (Stream<Path> paths = Files.walk(path)) {
			paths
			.filter(Files::isRegularFile)
			.map(Path::toFile)
			.forEach(file -> {
				try {
					Multimap<Map.Entry<Integer, Integer>, UUID> chunks = new AdaptationUtils.RegionFile(file.toPath()).getCustomDecors();
					for (Map.Entry<Map.Entry<Integer, Integer>, UUID> chunkEntry : chunks.entries()) {
						Map.Entry<Integer, Integer> loc = chunkEntry.getKey();
						Chunk chunk = world.getChunkAt(loc.getKey(), loc.getValue());
						chunk.load();
						Entity entity = world.getEntity(chunkEntry.getValue());
						if (entity instanceof ArmorStand armorStand) {
							ItemStack itemStack = armorStand.getEquipment().getHelmet();
							armorStand.getEquipment().setHelmet(this.updateCustomDecorEntityItem(itemStack, entity));
						} else if (entity instanceof ItemFrame itemFrame) {
							ItemStack itemStack = itemFrame.getItem();
							itemFrame.setItem(this.updateCustomDecorEntityItem(itemStack, entity));
						}
						chunk.unload();
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private @Nullable ItemStack updateCustomDecorEntityItem(@Nullable ItemStack itemStack, Entity entity) {
		if (itemStack == null || itemStack.getType().isAir()) return null;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (
				itemMeta != null
				&& !itemMeta.getPersistentDataContainer().has(MSDecorUtils.CUSTOM_DECOR_TYPE_NAMESPACED_KEY)
		) {
			CustomDecorData customDecorData = this.customDecorMap.getBySecondaryKey(itemMeta.getCustomModelData());
			if (customDecorData != null) {
				itemMeta.getPersistentDataContainer().set(
						MSDecorUtils.CUSTOM_DECOR_TYPE_NAMESPACED_KEY,
						PersistentDataType.STRING,
						customDecorData.getNamespacedKey().getKey()
				);
				itemStack.setItemMeta(itemMeta);
				Bukkit.getLogger().info(
						"Updated " + entity.getType() + " at : \n"
						+ entity.getLocation()
				);
			}
		}
		return itemStack;
	}

	public void updateTileEntities(@NotNull World world) {
		Path path = Paths.get(world.getWorldFolder().getAbsolutePath() + "/region");
		if (!path.toFile().exists()) return;
		try (Stream<Path> paths = Files.walk(path)) {
			paths
			.filter(Files::isRegularFile)
			.map(Path::toFile)
			.forEach(file -> {
				try {
					Multimap<Map.Entry<Integer, Integer>, Location> chunks = new AdaptationUtils.RegionFile(file.toPath()).getTileEntitiesWithCustoms(world);
					for (Map.Entry<Map.Entry<Integer, Integer>, Location> chunkEntry : chunks.entries()) {
						Map.Entry<Integer, Integer> loc = chunkEntry.getKey();
						Chunk chunk = world.getChunkAt(loc.getKey(), loc.getValue());
						chunk.load();
						Block block = world.getBlockAt(chunkEntry.getValue());
						BlockState blockState = block.getState();

						System.out.println(block.getType() + " " + (blockState instanceof BlockInventoryHolder));
						if (blockState instanceof BlockInventoryHolder holder) {
							int i = 0;
							ItemStack[] contents = holder.getInventory().getContents();
							for (ItemStack itemStack : contents) {
								if (this.updateItem(itemStack)) i++;
							}
							holder.getInventory().setContents(contents);
							if (i != 0) {
								Bukkit.getLogger().info(
										"Updated " + block.getType() + " at : \n"
										+ chunkEntry.getValue()
										+ "\nWith : " + i + " customs"
								);
							}
						}
						chunk.unload();
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void updatePlayers() {
		for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
			Player player = AdaptationUtils.loadPlayer(offlinePlayer);
			if (player != null) {
				int i = 0;

				ItemStack[] inventoryContents = player.getInventory().getContents();
				for (ItemStack itemStack : inventoryContents) {
					if (this.updateItem(itemStack)) i++;
				}
				player.getInventory().setContents(inventoryContents);

				ItemStack[] enderContents = player.getEnderChest().getContents();
				for (ItemStack itemStack : enderContents) {
					if (this.updateItem(itemStack)) i++;
				}
				player.getEnderChest().setContents(enderContents);

				if (i != 0) {
					Bukkit.getLogger().info(
							"Updated " + player.getName() + " (" + player.getUniqueId() + ") "
							+ "\nWith : " + i + " customs"
					);
				}
			}
		}
	}

	private boolean updateItem(@Nullable ItemStack itemStack) {
		if (itemStack == null || !itemStack.hasItemMeta()) return false;
		ItemMeta itemMeta = itemStack.getItemMeta();

		if (
				itemMeta instanceof BlockStateMeta blockStateMeta
				&& blockStateMeta.getBlockState() instanceof BlockInventoryHolder holder
		) {
			ItemStack[] contents = holder.getInventory().getContents();
			for (ItemStack item : contents) {
				this.updateItem(item);
			}
			holder.getInventory().setContents(contents);
			blockStateMeta.setBlockState((BlockState) holder);
			return itemStack.setItemMeta(blockStateMeta);
		}

		if (!itemStack.getItemMeta().hasCustomModelData()) return false;

		Object custom = this.getCustom(itemStack);
		System.out.println(custom);
		if (
				custom instanceof CustomDecorData data
				&& !itemMeta.getPersistentDataContainer().has(MSDecorUtils.CUSTOM_DECOR_TYPE_NAMESPACED_KEY)
		) {
			itemMeta.getPersistentDataContainer().set(
					MSDecorUtils.CUSTOM_DECOR_TYPE_NAMESPACED_KEY,
					PersistentDataType.STRING,
					data.getNamespacedKey().getKey()
			);
			return itemStack.setItemMeta(itemMeta);
		} else if (
				custom instanceof CustomBlockData data
				&& !itemMeta.getPersistentDataContainer().has(MSBlockUtils.CUSTOM_BLOCK_TYPE_NAMESPACED_KEY)
		) {
			itemMeta.getPersistentDataContainer().set(
					MSBlockUtils.CUSTOM_BLOCK_TYPE_NAMESPACED_KEY,
					PersistentDataType.STRING,
					data.getNamespacedKey().getKey()
			);
			return itemStack.setItemMeta(itemMeta);
		} else if (
				custom instanceof CustomItem data
				&& !itemMeta.getPersistentDataContainer().has(MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY)
		) {
			itemMeta.getPersistentDataContainer().set(
					MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY,
					PersistentDataType.STRING,
					data.getNamespacedKey().getKey()
			);
			return itemStack.setItemMeta(itemMeta);
		} else if (
				custom instanceof RenameableItem data
				&& !itemMeta.getPersistentDataContainer().has(MSItemUtils.CUSTOM_ITEM_RENAMEABLE_NAMESPACED_KEY)
		) {
			itemMeta.getPersistentDataContainer().set(
					MSItemUtils.CUSTOM_ITEM_RENAMEABLE_NAMESPACED_KEY,
					PersistentDataType.STRING,
					data.getNamespacedKey().getKey()
			);
			return itemStack.setItemMeta(itemMeta);
		}

		return false;
	}

	private @Nullable Object getCustom(@NotNull ItemStack itemStack) {
		int cmd = itemStack.getItemMeta().getCustomModelData();

		Object customBlock = this.customBlockMap.getBySecondaryKey(cmd);
		System.out.println(
				cmd + " " + (customBlock != null ? ((CustomBlockData) customBlock).craftItemStack().getItemMeta().getCustomModelData() : 0)
		);
		if (
				customBlock instanceof CustomBlockData data
				&& itemStack.getType() == data.craftItemStack().getType()
		) return data;

		Object customDecor = this.customDecorMap.getBySecondaryKey(cmd);
		if (
				customDecor instanceof CustomDecorData data
				&& itemStack.getType() == data.getItemStack().getType()
		) return data;

		Object customItem = this.customItemMap.getBySecondaryKey(cmd);
		if (
				customItem instanceof CustomItem data
				&& itemStack.getType() == data.getItemStack().getType()
		) return data;

		Object renameableItem = this.renameableItemMap.getBySecondaryKey(cmd);
		if (
				renameableItem instanceof RenameableItem data
				&& itemStack.getType() == data.getResultItemStack().getType()
		) return data;
		return null;
	}
}
