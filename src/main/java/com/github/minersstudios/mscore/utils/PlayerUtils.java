package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import com.github.minersstudios.msutils.utils.MSPlayerUtils;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;

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
	 * @param args     message
	 */
	public static void setSitting(@NotNull Player player, @NotNull Location location, String @Nullable ... args) {
		MSPlayerUtils.getPlayerInfo(player).setSitting(location, args);
	}

	/**
	 * Unsets the sitting position of the player
	 *
	 * @param player player who is currently sitting
	 * @param args   message
	 */
	public static void unsetSitting(@NotNull Player player, String @Nullable ... args) {
		MSPlayerUtils.getPlayerInfo(player).unsetSitting(args);
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

	/**
	 * Gets UUID from player nickname
	 *
	 * @param nickname player nickname
	 * @return player UUID
	 */
	@Nullable
	public static UUID getUUID(@NotNull String nickname) {
		if (MSCore.getConfigCache().onlineMode) {
			try {
				String UUIDJson = IOUtils.toString(new URL("https://api.mojang.com/users/profiles/minecraft/" + nickname), Charset.defaultCharset());
				if (UUIDJson.isEmpty()) return null;
				return UUID.fromString(((JSONObject) JSONValue.parseWithException(UUIDJson)).get("id").toString().replaceFirst(
						"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
						"$1-$2-$3-$4-$5"
				));
			} catch (IOException | ParseException e) {
				throw new RuntimeException(e);
			}
		} else {
			return UUID.nameUUIDFromBytes(("OfflinePlayer:" + nickname).getBytes(Charsets.UTF_8));
		}
	}

	/**
	 * Gets offline player by nickname
	 *
	 * @param nickname player nickname
	 * @return offline player
	 */
	public static @Nullable OfflinePlayer getOfflinePlayerByNick(@NotNull String nickname) {
		UUID uuid = getUUID(nickname);
		return uuid != null ? getOfflinePlayer(uuid, nickname) : null;
	}

	/**
	 * Gets offline player by uuid and nickname
	 *
	 * @param uuid player unique id
	 * @param name player nickname
	 * @return offline player
	 */
	public static @NotNull OfflinePlayer getOfflinePlayer(
			@NotNull UUID uuid,
			@NotNull String name
	) {
		CraftServer craftServer = (CraftServer) Bukkit.getServer();
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		if (offlinePlayer.getName() == null) {
			offlinePlayer = craftServer.getOfflinePlayer(new GameProfile(uuid, name));
		}
		return offlinePlayer;
	}
}
