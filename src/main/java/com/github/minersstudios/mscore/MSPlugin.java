package com.github.minersstudios.mscore;

import com.github.minersstudios.mscore.tabcompleters.Empty;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "EmptyMethod"})
public abstract class MSPlugin extends JavaPlugin {
	protected File pluginFolder;
	protected File configFile;
	protected FileConfiguration newConfig;
	protected boolean loadedCustoms;
	protected Set<String> classNames;

	@Override
	public final void onLoad() {
		this.pluginFolder = new File("config/minersstudios/" + this.getName() + "/");
		this.configFile = new File(this.pluginFolder, "config.yml");
		this.loadedCustoms = false;

		try (JarFile jarFile = new JarFile(this.getFile())) {
			this.classNames = jarFile.stream()
					.map(JarEntry::getName)
					.filter(name -> name.endsWith(".class"))
					.map(name -> name.replace("/", ".").replace(".class", ""))
					.collect(Collectors.toSet());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			Field field = JavaPlugin.class.getDeclaredField("dataFolder");
			field.setAccessible(true);
			field.set(this, this.pluginFolder);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		this.load();
	}

	@Override
	public final void onEnable() {
		long time = System.currentTimeMillis();
		try {
			this.loadListeners();
			this.registerCommands();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		this.enable();
		if (this.isEnabled()) {
			this.getLogger().log(Level.INFO, "\033[0;92mEnabled in " + (System.currentTimeMillis() - time) + "ms");
		}
	}

	@Override
	public final void onDisable() {
		long time = System.currentTimeMillis();
		this.disable();
		if (!this.isEnabled()) {
			this.getLogger().log(Level.INFO, "\033[0;92mDisabled in " + (System.currentTimeMillis() - time) + "ms");
		}
	}

	@Override
	public @NotNull FileConfiguration getConfig() {
		if (this.newConfig == null) {
			this.reloadConfig();
		}
		return this.newConfig;
	}

	@Override
	public void reloadConfig() {
		this.newConfig = YamlConfiguration.loadConfiguration(this.configFile);

		InputStream defConfigStream = this.getResource("config.yml");
		if (defConfigStream == null) return;

		this.newConfig.setDefaults(
				YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8))
		);
	}

	@Override
	public void saveConfig() {
		try {
			this.getConfig().save(this.configFile);
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
			} else {
				this.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
			}
		} catch (IOException ex) {
			this.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

	@Override
	public void saveDefaultConfig() {
		if (!this.configFile.exists()) {
			this.saveResource("config.yml", false);
		}
	}

	/**
	 * Registers all command in the project that is annotated with {@link MSCommand}
	 * <p>
	 * All commands must be implemented using {@link MSCommandExecutor} and located in the "com.github.minersstudios.plugin-name.commands" folder
	 *
	 * @throws ClassNotFoundException If the class was not found
	 */
	public void registerCommands() throws ClassNotFoundException {
		for (String className : this.classNames) {
			if (StringUtil.startsWithIgnoreCase(className, "com.github.minersstudios." + this.getName() + ".commands")) {
				Class<?> clazz = this.getClassLoader().loadClass(className);
				MSCommand msCommand = clazz.getAnnotation(MSCommand.class);
				if (msCommand != null) {
					try {
						if (clazz.getDeclaredConstructor().newInstance() instanceof MSCommandExecutor msCommandExecutor) {
							this.registerCommand(msCommand, msCommandExecutor, msCommandExecutor);
						} else {
							this.getLogger().log(Level.WARNING, "Registered command that is not instance of MSCommandExecutor (" + className + ")");
						}
					} catch (Exception e) {
						this.getLogger().log(Level.SEVERE, "Failed to register command", e);
					}
				}
			}
		}
	}

	/**
	 * @param msCommand       command to be registered
	 * @param commandExecutor command executor
	 * @param tabCompleter    command tab completer, {@link Empty} if null
	 */
	public final void registerCommand(
			@NotNull MSCommand msCommand,
			@NotNull CommandExecutor commandExecutor,
			@Nullable TabCompleter tabCompleter
	) {
		String name = msCommand.command();
		PluginCommand bukkitCommand = this.getCommand(name);
		PluginCommand pluginCommand = bukkitCommand == null ? createCommand(name) : bukkitCommand;

		List<String> aliases = Arrays.asList(msCommand.aliases());
		if (!aliases.isEmpty()) {
			pluginCommand.setAliases(aliases);
		}

		String usage = msCommand.usage();
		if (!usage.isEmpty()) {
			pluginCommand.setUsage(usage);
		}

		String description = msCommand.description();
		if (!description.isEmpty()) {
			pluginCommand.setDescription(description);
		}

		String permissionStr = msCommand.permission();
		if (!permissionStr.isEmpty()) {
			Map<String, Boolean> children = new HashMap<>();
			String[] keys = msCommand.permissionParentKeys();
			boolean[] values = msCommand.permissionParentValues();
			if (keys.length != values.length) {
				throw new IllegalArgumentException("Permission and boolean array lengths do not match in command : " + name);
			} else {
				for (int i = 0; i < keys.length; i++) {
					children.put(keys[i], values[i]);
				}
			}

			if (Bukkit.getPluginManager().getPermission(permissionStr) == null) {
				Permission permission = new Permission(permissionStr, msCommand.permissionDefault(), children);
				Bukkit.getPluginManager().addPermission(permission);
			}
			pluginCommand.setPermission(permissionStr);
		}

		pluginCommand.setExecutor(commandExecutor);
		pluginCommand.setTabCompleter(tabCompleter);
		Bukkit.getCommandMap().register(this.getName(), pluginCommand);
	}

	private @NotNull PluginCommand createCommand(String command) {
		PluginCommand pluginCommand;
		try {
			Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			constructor.setAccessible(true);
			pluginCommand = constructor.newInstance(command, this);
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		}
		return pluginCommand;
	}

	/**
	 * Loads all listeners in the project that is annotated with {@link MSListener}
	 * <p>
	 * All listeners must be implemented using {@link Listener} and located in the "com.github.minersstudios.plugin-name.listeners" folder
	 *
	 * @throws ClassNotFoundException If the class was not found
	 */
	public void loadListeners() throws ClassNotFoundException {
		for (String className : this.getClassNames()) {
			if (StringUtil.startsWithIgnoreCase(className, "com.github.minersstudios." + this.getName() + ".listeners")) {
				Class<?> clazz = this.getClassLoader().loadClass(className);
				if (clazz.isAnnotationPresent(MSListener.class)) {
					try {
						if (clazz.getDeclaredConstructor().newInstance() instanceof Listener listener) {
							this.getServer().getPluginManager().registerEvents(listener, this);
						} else {
							this.getLogger().log(Level.WARNING, "Registered listener that is not instance of Listener (" + className + ")");
						}
					} catch (Exception e) {
						this.getLogger().log(Level.SEVERE, "Failed to load listener", e);
					}
				}
			}
		}
	}

	/**
	 * Gathers the names of all plugin classes and converts them to a package-like string
	 * <p>
	 * "com/example/Example.class" -> "com.example.Example"
	 *
	 * @return plugin class names
	 */
	public final @NotNull Set<String> getClassNames() {
		return this.classNames;
	}

	public void load() {}

	public void enable() {}

	public void disable() {}

	@Contract(pure = true)
	public final @NotNull File getConfigFile() {
		return this.configFile;
	}

	@Contract(pure = true)
	public final @NotNull File getPluginFolder() {
		return this.pluginFolder;
	}

	@Contract(pure = true)
	public final boolean isLoadedCustoms() {
		return this.loadedCustoms;
	}

	public final void setLoadedCustoms(boolean loadedCustoms) {
		this.loadedCustoms = loadedCustoms;
	}
}
