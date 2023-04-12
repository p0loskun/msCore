package com.github.minersstudios.mscore.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public final class PlayerUtils {

	@Contract(value = " -> fail")
	private PlayerUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Sets player to a seated position underneath him
	 *
	 * @param player   player
	 */
	public static void setSitting(@NotNull Player player) {
		setSitting(player, player.getLocation());
	}

	/**
	 * Sets player to a seated position in specified location
	 *
	 * @param player   player
	 * @param location location where the player will sit
	 */
	public static void setSitting(@NotNull Player player, @NotNull Location location) {
		com.github.minersstudios.msutils.utils.PlayerUtils.setSitting(player, location, null);
	}

	/**
	 * Unsets the sitting position of the player
	 *
	 * @param player player who is currently sitting
	 */
	public static void unsetSitting(@NotNull Player player) {
		com.github.minersstudios.msutils.utils.PlayerUtils.setSitting(player, null, null);
	}

	/**
	 * @param offlinePlayer offline player whose data will be loaded
	 * @return Online player from offline player
	 */
	public static @Nullable Player loadPlayer(@NotNull OfflinePlayer offlinePlayer) {
		if (!offlinePlayer.hasPlayedBefore()) return null;
		GameProfile profile = new GameProfile(
				offlinePlayer.getUniqueId(),
				offlinePlayer.getName() != null
						? offlinePlayer.getName()
						: offlinePlayer.getUniqueId().toString()
		);
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		ServerLevel worldServer = server.getLevel(Level.OVERWORLD);

		if (worldServer == null) return null;

		Player online = new ServerPlayer(server, worldServer, profile).getBukkitEntity();
		online.loadData();
		return online;
	}
}
