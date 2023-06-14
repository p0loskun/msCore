package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;

@SuppressWarnings("unused")
public final class DateUtils {

	@Contract(value = " -> fail")
	private DateUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Gets the date at the address
	 *
	 * @param date    date to be converted
	 * @param address address
	 * @return string date format
	 */
	public static @NotNull String getDate(
			@NotNull Date date,
			@Nullable InetAddress address
	) {
		Instant milli = Instant.ofEpochMilli(date.getTime());
		ZoneId zoneId = ZoneId.systemDefault();

		if (address == null) {
			return milli.atZone(zoneId).format(MSCore.getConfigCache().timeFormatter);
		}

		String timeZone = getTimezone(address);
		return milli.atZone(
				ZoneId.of(timeZone.equalsIgnoreCase("Europe/Kyiv")
						? "Europe/Kiev"
						: timeZone
				)).format(MSCore.getConfigCache().timeFormatter);
	}

	/**
	 * Gets date with sender time zone
	 *
	 * @param date   date to be converted
	 * @param sender sender (can be player)
	 * @return string date format
	 */
	public static @NotNull String getSenderDate(
			@NotNull Date date,
			@NotNull CommandSender sender
	) {
		if (sender instanceof Player player) {
			InetSocketAddress socketAddress = player.getAddress();
			return getDate(date, socketAddress != null ? socketAddress.getAddress() : null);
		}
		return getDate(date, null);
	}

	/**
	 * Gets timezone from ip
	 *
	 * @param ip IP address
	 * @return timezone from ip
	 */
	public static @NotNull String getTimezone(@NotNull InetAddress ip) {
		try (InputStream input = new URL("http://ip-api.com/json/" + ip.getHostAddress()).openStream()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			StringBuilder entirePage = new StringBuilder();

			String inputLine;
			while ((inputLine = reader.readLine()) != null) {
				entirePage.append(inputLine);
			}

			reader.close();
			input.close();

			String pageString = entirePage.toString();
			return pageString.contains("\"timezone\":\"")
					? pageString.split("\"timezone\":\"")[1].split("\",")[0]
					: ZoneId.systemDefault().toString();
		} catch (IOException e) {
			MSCore.getInstance().getLogger().log(Level.WARNING, e.getMessage());
			return ZoneId.systemDefault().toString();
		}
	}
}
