package com.github.minersstudios.mscore.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class PlayerUtils {

	@Contract(value = " -> fail")
	private PlayerUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static void setSitting(@NotNull Player player) {
		setSitting(player, player.getLocation());
	}

	public static void setSitting(@NotNull Player player, @NotNull Location location) {
		com.github.minersstudios.msutils.utils.PlayerUtils.setSitting(player, location, null);
	}

	public static void unsetSitting(@NotNull Player player) {
		com.github.minersstudios.msutils.utils.PlayerUtils.setSitting(player, null, null);
	}
}
