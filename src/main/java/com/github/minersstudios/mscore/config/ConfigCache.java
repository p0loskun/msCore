package com.github.minersstudios.mscore.config;

import com.github.minersstudios.mscore.MSCore;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class ConfigCache {
	public final File dataFile;
	public final YamlConfiguration yamlConfiguration;

	public final @NotNull DateTimeFormatter timeFormatter;

	public ConfigCache() {
		this.dataFile = new File(MSCore.getInstance().getDataFolder(), "config.yml");
		this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.dataFile);

		this.timeFormatter = DateTimeFormatter.ofPattern(this.yamlConfiguration.getString("date-format", "EEE, yyyy-MM-dd HH:mm z"));
	}
}
