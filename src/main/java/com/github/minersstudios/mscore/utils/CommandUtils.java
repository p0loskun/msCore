package com.github.minersstudios.mscore.utils;

import com.mojang.brigadier.tree.CommandNode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public final class CommandUtils {
	private static final Field CHILDREN;
	private static final Field LITERALS;
	private static final Field ARGUMENTS;

	static {
		try {
			CHILDREN = CommandNode.class.getDeclaredField("children");
			LITERALS = CommandNode.class.getDeclaredField("literals");
			ARGUMENTS = CommandNode.class.getDeclaredField("arguments");
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		CHILDREN.setAccessible(true);
		LITERALS.setAccessible(true);
		ARGUMENTS.setAccessible(true);
	}

	@Contract(value = " -> fail")
	private CommandUtils() {
		throw new IllegalStateException("Utility class");
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
		if (!string.matches("\\d+[smhdMy]")) return null;
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

	public static void removeCommand(@NotNull CommandNode<?> root, @NotNull String name) {
		try {
			((Map<?, ?>) CHILDREN.get(root)).remove(name);
			((Map<?, ?>) LITERALS.get(root)).remove(name);
			((Map<?, ?>) ARGUMENTS.get(root)).remove(name);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
