package com.github.minersstudios.mscore;

import com.github.minersstudios.msblock.customblock.CustomBlockData;
import com.github.minersstudios.mscore.config.ConfigCache;
import com.github.minersstudios.mscore.utils.MSPluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class MSCore extends MSPlugin {
	private static MSCore instance;
	private static ConfigCache configCache;

	@Override
	public void enable() {
		instance = this;

		reloadConfigs();
	}

	public static void reloadConfigs() {
		instance.saveDefaultConfig();
		instance.reloadConfig();
		configCache = new ConfigCache();

		if (configCache.updateItemsNBT) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (MSPluginUtils.isLoadedCustoms()) {
						for (World world : Bukkit.getWorlds()) {
							configCache.updateCustomDecors(world);
							configCache.updateTileEntities(world);
						}
						configCache.updatePlayers();
						this.cancel();
					}
				}
			}.runTaskTimer(instance, 0L, 10L);
		}
	}

	@Contract(pure = true)
	public static @NotNull MSCore getInstance() {
		return instance;
	}

	@Contract(pure = true)
	public static @NotNull ConfigCache getConfigCache() {
		return configCache;
	}
}
