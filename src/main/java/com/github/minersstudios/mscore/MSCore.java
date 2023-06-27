package com.github.minersstudios.mscore;

import com.github.minersstudios.mscore.config.ConfigCache;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class MSCore extends MSPlugin {
    private static MSCore instance;
    private static ConfigCache configCache;

    @Override
    public void enable() {
        instance = this;

        this.reloadConfigs();
    }

    public void reloadConfigs() {
        this.saveDefaultConfig();
        this.reloadConfig();
        configCache = new ConfigCache(this.getConfigFile());
    }

    @Contract(pure = true)
    public static @NotNull ConfigCache getConfigCache() {
        return configCache;
    }

    @Contract(pure = true)
    public static @NotNull MSCore getInstance() {
        return instance;
    }
}
