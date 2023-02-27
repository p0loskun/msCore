package com.github.minersstudios.mscore.config;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.mscore.collections.DualMap;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msitems.items.CustomItem;
import com.github.minersstudios.msitems.items.RenameableItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public final class ConfigCache {
	public final File dataFile;
	public final YamlConfiguration yamlConfiguration;

	public final @NotNull DateTimeFormatter timeFormatter;

	public final DualMap<Integer, String, CustomDecorData> customDecorMap = new DualMap<>();
	public final List<Recipe> customDecorRecipes = new ArrayList<>();

	public final DualMap<Integer, String, CustomBlockData> customBlockMap = new DualMap<>();
	public final Map<Integer, CustomBlockData> cachedNoteBlockData = new HashMap<>();
	public final List<Recipe> customBlockRecipes = new ArrayList<>();

	public final DualMap<Integer, String, CustomItem> customItemMap = new DualMap<>();
	public final DualMap<Integer, String, RenameableItem> renameableItemMap = new DualMap<>();
	public final List<RenameableItem> renameableItemsMenu = new ArrayList<>();
	public final List<Recipe> customItemRecipes = new ArrayList<>();

	public ConfigCache() {
		this.dataFile = new File(MSCore.getInstance().getDataFolder(), "config.yml");
		this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.dataFile);

		this.timeFormatter = DateTimeFormatter.ofPattern(this.yamlConfiguration.getString("date-format", "EEE, yyyy-MM-dd HH:mm z"));
	}
}
