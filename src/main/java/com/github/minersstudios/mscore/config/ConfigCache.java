package com.github.minersstudios.mscore.config;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.collections.DualMap;
import com.github.minersstudios.mscore.inventory.CustomInventory;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msitems.items.CustomItem;
import com.github.minersstudios.msitems.items.RenameableItem;
import com.mojang.brigadier.tree.CommandNode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings("unused")
public final class ConfigCache {
	public final @NotNull File dataFile;
	public final @NotNull YamlConfiguration yamlConfiguration;

	public final @NotNull DateTimeFormatter timeFormatter;

	public final @NotNull DualMap<String, Integer, CustomDecorData> customDecorMap = new DualMap<>();
	public final @NotNull List<Recipe> customDecorRecipes = new ArrayList<>();

	public final @NotNull DualMap<String, Integer, CustomBlockData> customBlockMap = new DualMap<>();
	public final @NotNull Map<Integer, CustomBlockData> cachedNoteBlockData = new HashMap<>();
	public final @NotNull List<Recipe> customBlockRecipes = new ArrayList<>();

	public final @NotNull DualMap<String, Integer, CustomItem> customItemMap = new DualMap<>();
	public final @NotNull DualMap<String, Integer, RenameableItem> renameableItemMap = new DualMap<>();
	public final @NotNull List<RenameableItem> renameableItemsMenu = new ArrayList<>();
	public final @NotNull List<Recipe> customItemRecipes = new ArrayList<>();

	public final @NotNull HashMap<String, CustomInventory> customInventories = new HashMap<>();

	public static final Map<CommandNode<? extends BukkitBrigadierCommandSource>, String> COMMANDS = new HashMap<>();

	public boolean onlineMode;

	public ConfigCache() {
		this.dataFile = MSCore.getInstance().getConfigFile();
		this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.dataFile);

		this.timeFormatter = DateTimeFormatter.ofPattern(this.yamlConfiguration.getString("date-format", "EEE, yyyy-MM-dd HH:mm z"));
		this.onlineMode = getOnlineMode();
	}

	private static boolean getOnlineMode() {
		try (InputStream input = new FileInputStream("server.properties")) {
			Properties properties = new Properties();
			properties.load(input);
			input.close();
			return Boolean.parseBoolean(properties.getProperty("online-mode"));
		} catch (IOException e) {
			throw new SecurityException(e);
		}
	}
}
