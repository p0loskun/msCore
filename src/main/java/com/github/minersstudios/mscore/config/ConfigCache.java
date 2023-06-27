package com.github.minersstudios.mscore.config;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.collections.DualMap;
import com.github.minersstudios.mscore.inventory.CustomInventoryMap;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msitems.items.CustomItem;
import com.github.minersstudios.msitems.items.RenameableItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings("unused")
public final class ConfigCache {
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

    public final @NotNull CustomInventoryMap customInventoryMap;

    public final Map<String, UUID> playerUUIDs = new HashMap<>();

    public ConfigCache(@NotNull File configFile) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
        this.timeFormatter = DateTimeFormatter.ofPattern(yamlConfiguration.getString("date-format", "EEE, yyyy-MM-dd HH:mm z"));

        this.customInventoryMap = new CustomInventoryMap();
    }
}
