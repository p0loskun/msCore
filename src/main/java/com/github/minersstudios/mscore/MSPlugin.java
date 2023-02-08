package com.github.minersstudios.mscore;

import com.github.minersstudios.mscore.utils.MSListener;
import com.google.common.base.Charsets;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public abstract class MSPlugin extends JavaPlugin {
	private final File pluginFolder;
	private final File configFile;
	private FileConfiguration newConfig;

	protected MSPlugin() {
		this.pluginFolder = new File("config/minersstudios/" + this.getName() + "/");
		this.configFile = new File(pluginFolder, "config.yml");
	}

	@Override
	public final void onEnable() {
		long time = System.currentTimeMillis();
		this.enable();
		try {
			this.loadListeners("com.github.minersstudios." + this.getName() + ".listeners");
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		if (this.isEnabled()) {
			this.getLogger().log(Level.INFO, ChatColor.GREEN + "Enabled in " + (System.currentTimeMillis() - time) + "ms");
		}
	}

	@Override
	public final void onDisable() {
		long time = System.currentTimeMillis();
		this.disable();
		if (!this.isEnabled()) {
			this.getLogger().log(Level.INFO, ChatColor.GREEN + "Disabled in " + (System.currentTimeMillis() - time) + "ms");
		}
	}

	@Override
	public @NotNull FileConfiguration getConfig() {
		if (this.newConfig == null) {
			reloadConfig();
		}
		return this.newConfig;
	}

	@Override
	public void reloadConfig() {
		this.newConfig = YamlConfiguration.loadConfiguration(this.configFile);

		InputStream defConfigStream = this.getResource("config.yml");
		if (defConfigStream == null) return;

		this.newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
	}

	@Override
	public void saveConfig() {
		try {
			getConfig().save(this.configFile);
		} catch (IOException ex) {
			this.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, ex);
		}
	}

	@Override
	public void saveResource(@NotNull String resourcePath, boolean replace) {
		if (resourcePath.isEmpty()) {
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
		}

		resourcePath = resourcePath.replace('\\', '/');
		InputStream in = this.getResource(resourcePath);

		if (in == null) return;

		File outFile = new File(this.pluginFolder, resourcePath);
		File outDir = new File(this.pluginFolder, resourcePath.substring(0, Math.max(resourcePath.lastIndexOf('/'), 0)));

		if (!outDir.exists()) {
			boolean mkdir = outDir.mkdirs();
			if (!mkdir) {
				throw new SecurityException("Directory creation failed");
			}
		}

		try {
			if (!outFile.exists() || replace) {
				OutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			}
		} catch (IOException e) {
			throw new SecurityException(e);
		}
	}

	@Override
	public void saveDefaultConfig() {
		if (!this.configFile.exists()) {
			this.saveResource("config.yml", false);
		}
	}

	private void loadListeners(@NotNull String packageName) throws IOException, ClassNotFoundException {
		for (String className : getClassNamesFromJarFile(this.getFile())) {
			if (StringUtil.startsWithIgnoreCase(className, packageName)) {
				Class<?> clazz = this.getClassLoader().loadClass(className);
				if (clazz.isAnnotationPresent(MSListener.class)) {
					try {
						if (clazz.getDeclaredConstructor().newInstance() instanceof Listener listener) {
							this.getServer().getPluginManager().registerEvents(listener, this);
						} else {
							this.getLogger().log(Level.WARNING, "Registered listener that is not instance of Listener (" + clazz.getName() + ")");
						}
					} catch (Exception e) {
						this.getLogger().log(Level.SEVERE, "Failed to load listener", e);
					}
				}
			}
		}
	}

	private static Set<String> getClassNamesFromJarFile(File givenFile) {
		Set<String> classNames = new HashSet<>();
		try (JarFile jarFile = new JarFile(givenFile)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				String entryName = entries.nextElement().getName();
				if (entryName.endsWith(".class")) {
					classNames.add(entryName
							.replace("/", ".")
							.replace(".class", "")
					);
				}
			}
			return classNames;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void enable() {}

	public void disable() {}

	public @NotNull File getConfigFile() {
		return this.configFile;
	}

	public @NotNull File getPluginFolder() {
		return this.pluginFolder;
	}
}
