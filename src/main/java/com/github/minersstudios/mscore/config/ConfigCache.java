package com.github.minersstudios.mscore.config;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.collections.DualMap;
import com.github.minersstudios.mscore.utils.*;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msitems.items.CustomItem;
import com.github.minersstudios.msitems.items.RenameableItem;
import com.google.common.collect.Multimap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.BlockInventoryHolder;
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
	public final @NotNull File dataFile;
	public final @NotNull YamlConfiguration yamlConfiguration;

	public final @NotNull DateTimeFormatter timeFormatter;
	public final boolean updateItemsNBT;

	public final @NotNull DualMap<String, Integer, CustomDecorData> customDecorMap = new DualMap<>();
	public final @NotNull List<Recipe> customDecorRecipes = new ArrayList<>();

	public final @NotNull DualMap<String, Integer, CustomBlockData> customBlockMap = new DualMap<>();
	public final @NotNull Map<Integer, CustomBlockData> cachedNoteBlockData = new HashMap<>();
	public final @NotNull List<Recipe> customBlockRecipes = new ArrayList<>();

	public final @NotNull DualMap<String, Integer, CustomItem> customItemMap = new DualMap<>();
	public final @NotNull DualMap<String, Integer, RenameableItem> renameableItemMap = new DualMap<>();
	public final @NotNull List<RenameableItem> renameableItemsMenu = new ArrayList<>();
	public final @NotNull List<Recipe> customItemRecipes = new ArrayList<>();

	public ConfigCache() {
		this.dataFile = MSCore.getInstance().getConfigFile();
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
					Multimap<Map.Entry<Integer, Integer>, UUID> chunks = new RegionFile(file.toPath()).getCustomDecors();
					for (Map.Entry<Map.Entry<Integer, Integer>, UUID> chunkEntry : chunks.entries()) {
						Map.Entry<Integer, Integer> loc = chunkEntry.getKey();
						Chunk chunk = world.getChunkAt(loc.getKey(), loc.getValue());
						chunk.load();
						Entity entity = world.getEntity(chunkEntry.getValue());
						if (entity instanceof ArmorStand armorStand) {
							ItemStack itemStack = armorStand.getEquipment().getHelmet();
							armorStand.getEquipment().setHelmet(this.updateEntityItem(itemStack, entity));
						} else if (entity instanceof ItemFrame itemFrame) {
							ItemStack itemStack = itemFrame.getItem();
							itemFrame.setItem(this.updateEntityItem(itemStack, entity));
						} else if (entity instanceof Item item) {
							ItemStack itemStack = item.getItemStack();
							ItemStack newItem = this.updateEntityItem(itemStack, entity);
							if (newItem != null) {
								item.setItemStack(newItem);
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

	@Contract("null, _ -> null")
	private @Nullable ItemStack updateEntityItem(@Nullable ItemStack itemStack, @NotNull Entity entity) {
		if (itemStack == null || itemStack.getType().isAir()) return null;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta != null) {
			if (this.updateItem(itemStack) != 0) {
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
					Multimap<Map.Entry<Integer, Integer>, Location> chunks = new RegionFile(file.toPath()).getTileEntitiesWithCustoms(world);
					for (Map.Entry<Map.Entry<Integer, Integer>, Location> chunkEntry : chunks.entries()) {
						Map.Entry<Integer, Integer> loc = chunkEntry.getKey();
						Chunk chunk = world.getChunkAt(loc.getKey(), loc.getValue());
						chunk.load();
						Block block = world.getBlockAt(chunkEntry.getValue());
						BlockState blockState = block.getState();

						if (blockState instanceof BlockInventoryHolder holder) {
							int c = 0;
							ItemStack[] contents = holder.getInventory().getContents();
							for (ItemStack itemStack : contents) {
								c += this.updateItem(itemStack);
							}
							holder.getInventory().setContents(contents);
							if (c != 0) {
								Bukkit.getLogger().info(
										"Updated " + block.getType() + " at : \n"
										+ chunkEntry.getValue()
										+ "\nWith : " + c + " customs"
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
			Player player = PlayerUtils.loadPlayer(offlinePlayer);
			if (player != null) {
				int c = 0;

				ItemStack[] inventoryContents = player.getInventory().getContents();
				for (ItemStack itemStack : inventoryContents) {
					c += this.updateItem(itemStack);
				}
				player.getInventory().setContents(inventoryContents);

				ItemStack[] enderContents = player.getEnderChest().getContents();
				for (ItemStack itemStack : enderContents) {
					c += this.updateItem(itemStack);
				}
				player.getEnderChest().setContents(enderContents);

				if (c != 0) {
					Bukkit.getLogger().info(
							"Updated " + player.getName() + " (" + player.getUniqueId() + ") "
							+ "\nWith : " + c + " customs"
					);
					player.saveData();
				}
			}
		}
	}

	private int updateItem(@Nullable ItemStack itemStack) {
		if (itemStack == null || !itemStack.hasItemMeta()) return 0;
		ItemMeta itemMeta = itemStack.getItemMeta();

		if (
				itemMeta instanceof BlockStateMeta blockStateMeta
				&& blockStateMeta.getBlockState() instanceof BlockInventoryHolder holder
		) {
			int i = 0;
			ItemStack[] contents = holder.getInventory().getContents();
			for (ItemStack item : contents) {
				i += this.updateItem(item);
			}
			holder.getInventory().setContents(contents);
			blockStateMeta.setBlockState((BlockState) holder);
			itemStack.setItemMeta(itemMeta);
			return i;
		}

		if (!itemStack.getItemMeta().hasCustomModelData()) return 0;

		Object custom = this.getCustom(itemStack);
		if (
				custom instanceof CustomDecorData data
				&& !itemMeta.getPersistentDataContainer().has(MSDecorUtils.CUSTOM_DECOR_TYPE_NAMESPACED_KEY)
		) {
			itemMeta.getPersistentDataContainer().set(
					MSDecorUtils.CUSTOM_DECOR_TYPE_NAMESPACED_KEY,
					PersistentDataType.STRING,
					data.getNamespacedKey().getKey()
			);
			itemStack.setItemMeta(itemMeta);
			return 1;
		} else if (
				custom instanceof CustomBlockData data
				&& !itemMeta.getPersistentDataContainer().has(MSBlockUtils.CUSTOM_BLOCK_TYPE_NAMESPACED_KEY)
		) {
			itemMeta.getPersistentDataContainer().set(
					MSBlockUtils.CUSTOM_BLOCK_TYPE_NAMESPACED_KEY,
					PersistentDataType.STRING,
					data.getNamespacedKey().getKey()
			);
			itemStack.setItemMeta(itemMeta);
			return 1;
		} else if (
				custom instanceof CustomItem data
				&& !itemMeta.getPersistentDataContainer().has(MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY)
		) {
			itemMeta.getPersistentDataContainer().set(
					MSItemUtils.CUSTOM_ITEM_TYPE_NAMESPACED_KEY,
					PersistentDataType.STRING,
					data.getNamespacedKey().getKey()
			);
			itemStack.setItemMeta(itemMeta);
			return 1;
		}

		return 0;
	}

	private @Nullable Object getCustom(@NotNull ItemStack itemStack) {
		int cmd = itemStack.getItemMeta().getCustomModelData();

		Object customBlock = this.customBlockMap.getBySecondaryKey(cmd);
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
		return null;
	}
}
