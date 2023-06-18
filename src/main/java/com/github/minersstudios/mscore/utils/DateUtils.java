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
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("unused")
public final class DateUtils {
	public static final String CHRONO_REGEX = "\\d+[smhdMy]";

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

	/**
	 * Gets date with player time zone
	 *
	 * @param date   date to be converted
	 * @param sender command sender
	 * @return string date format
	 */
	public static @NotNull String getSenderDate(
			@NotNull Date date,
			@Nullable CommandSender sender
	) {
		if (sender instanceof Player player) {
			InetSocketAddress socketAddress = player.getAddress();
			return DateUtils.getDate(date, socketAddress != null ? socketAddress.getAddress() : null);
		}
		return DateUtils.getDate(date, null);
	}

	/**
	 * Gets time suggestions from number
	 * <p>
	 * Used for command tab completer
	 *
	 * @param input number of time
	 * @return time suggestions
	 */
	public static @NotNull List<String> getTimeSuggestions(@NotNull String input) {
		List<String> suggestions = new ArrayList<>();
		if (!input.matches("\\d+")) return suggestions;
		suggestions.add(input + "s");
		suggestions.add(input + "m");
		suggestions.add(input + "h");
		suggestions.add(input + "d");
		suggestions.add(input + "M");
		suggestions.add(input + "y");
		return suggestions;
	}

	/**
	 * Gets a date with time added
	 * <p>
	 * Regex : \d+[smhdMy]
	 *
	 * @param string         time
	 * @param throwException if true, an exception will be thrown
	 * @return date with time added
	 * @throws NumberFormatException if the string does not contain a parsable long
	 * @throws DateTimeException     if the chrono unit value is too big and the addition cannot be made
	 * @throws ArithmeticException   if numeric overflow occurs
	 */
	public static @Nullable Date getDateFromString(
			@NotNull String string,
			boolean throwException
	) throws NumberFormatException, DateTimeException, ArithmeticException {
		if (!matchesChrono(string)) return null;

		String chronoUnit = string.replaceAll("\\d+", "");
		Instant instant = Instant.now();

		try {
			long number = Long.parseLong(string.replaceAll("[smhdMy]", ""));
			return Date.from(
					switch (chronoUnit) {
						case "s" -> instant.plus(number, ChronoUnit.SECONDS);
						case "m" -> instant.plus(number, ChronoUnit.MINUTES);
						case "h" -> instant.plus(number, ChronoUnit.HOURS);
						case "M" -> instant.plus(Math.multiplyExact(number, ChronoUnit.MONTHS.getDuration().toDays()), ChronoUnit.DAYS);
						case "y" -> instant.plus(Math.multiplyExact(number, ChronoUnit.YEARS.getDuration().toDays()), ChronoUnit.DAYS);
						default -> instant.plus(number, ChronoUnit.DAYS);
					}
			);
		} catch (DateTimeException | NumberFormatException | ArithmeticException e) {
			if (throwException) throw e;
			return null;
		}
	}

	/**
	 * Gets a date with time added
	 * <p>
	 * Regex : \d+[smhdMy]
	 *
	 * @param string time
	 * @return date with time added
	 * @throws NumberFormatException if the string does not contain a parsable long
	 * @throws DateTimeException     if the chrono unit value is too big and the addition cannot be made
	 * @throws ArithmeticException   if numeric overflow occurs
	 */
	public static @Nullable Date getDateFromString(@NotNull String string) throws NumberFormatException, DateTimeException, ArithmeticException {
		return getDateFromString(string, true);
	}

	@Contract(value = "null -> false", pure = true)
	public static boolean matchesChrono(@Nullable String string) {
		return string != null && string.matches(CHRONO_REGEX);
	}
}
