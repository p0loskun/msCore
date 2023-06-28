package com.github.minersstudios.mscore;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.collections.DualMap;
import com.github.minersstudios.mscore.config.Config;
import com.github.minersstudios.mscore.inventory.CustomInventoryMap;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msitems.items.CustomItem;
import com.github.minersstudios.msitems.items.RenameableItem;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

@SuppressWarnings("unused")
public final class Cache {
    public final @NotNull DualMap<String, Integer, CustomDecorData> customDecorMap = new DualMap<>();
    public final @NotNull List<Recipe> customDecorRecipes = new ArrayList<>();
    public final @NotNull DualMap<String, Integer, CustomBlockData> customBlockMap = new DualMap<>();
    public final @NotNull Map<Integer, CustomBlockData> cachedNoteBlockData = new HashMap<>();
    public final @NotNull List<Recipe> customBlockRecipes = new ArrayList<>();
    public final @NotNull DualMap<String, Integer, CustomItem> customItemMap = new DualMap<>();
    public final @NotNull DualMap<String, Integer, RenameableItem> renameableItemMap = new DualMap<>();
    public final @NotNull List<RenameableItem> renameableItemsMenu = new ArrayList<>();
    public final @NotNull List<Recipe> customItemRecipes = new ArrayList<>();
    public final @NotNull CustomInventoryMap customInventoryMap = new CustomInventoryMap();
    public final Map<String, UUID> playerUUIDs = new HashMap<>();
    public Config config;

    public Cache() {
        this.reloadConfigs();
    }

    public void reloadConfigs() {
        MSCore plugin = MSCore.getInstance();
        File configFile = plugin.getConfigFile();

        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = new Config(configFile);
    }
}
