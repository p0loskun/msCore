package com.github.minersstudios.mscore.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class CommandUtils {

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
}
