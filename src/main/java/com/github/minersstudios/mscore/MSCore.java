package com.github.minersstudios.mscore;

import com.github.minersstudios.mscore.config.ConfigCache;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
