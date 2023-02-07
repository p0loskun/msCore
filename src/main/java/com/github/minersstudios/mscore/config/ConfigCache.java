package com.github.minersstudios.mscore.config;

import com.github.minersstudios.mscore.MSCore;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigCache {
	public final File dataFile;
	public final YamlConfiguration yamlConfiguration;

	public final String dateFormat;

	public ConfigCache() {
		this.dataFile = new File(MSCore.getInstance().getDataFolder(), "config.yml");
		this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.dataFile);

		this.dateFormat = this.yamlConfiguration.getString("date-format");
	}
}
