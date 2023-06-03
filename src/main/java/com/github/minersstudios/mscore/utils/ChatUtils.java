package com.github.minersstudios.mscore.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class ChatUtils {
	public static final Style DEFAULT_STYLE = Style.style(
			NamedTextColor.WHITE,
			TextDecoration.OBFUSCATED.withState(false),
			TextDecoration.BOLD.withState(false),
			TextDecoration.ITALIC.withState(false),
			TextDecoration.STRIKETHROUGH.withState(false),
			TextDecoration.UNDERLINED.withState(false)
	);

	public static final Style COLORLESS_DEFAULT_STYLE = Style.style(
			TextDecoration.OBFUSCATED.withState(false),
			TextDecoration.BOLD.withState(false),
			TextDecoration.ITALIC.withState(false),
			TextDecoration.STRIKETHROUGH.withState(false),
			TextDecoration.UNDERLINED.withState(false)
	);

	@Contract(value = " -> fail")
	private ChatUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Sends info message to target
	 *
	 * @param target  target, if null sends to console
	 * @param message info message {@link Component}
	 */
	public static void sendInfo(
			@Nullable Object target,
			@NotNull Component message
	) {
		if (target instanceof Player player) {
			player.sendMessage(Component.text(" ").append(message));
		} else if (
				target instanceof CommandSender sender
				&& !(sender instanceof ConsoleCommandSender)
		) {
			sender.sendMessage(Component.text(" ").append(message));
		} else {
			Bukkit.getLogger().info(serializeLegacyComponent(message));
		}
	}

	/**
	 * Sends info message to console
	 *
	 * @param message info message {@link Component}
	 */
	public static void sendInfo(@NotNull Component message) {
		sendInfo(null, message);
	}


	/**
	 * Sends info message to target
	 *
	 * @param target  target, if null sends to console
	 * @param message info message {@link String}
	 */
	public static void sendInfo(
			@Nullable Object target,
			@NotNull String message
	) {
		sendInfo(target, Component.text(message));
	}

	/**
	 * Sends info message to console
	 *
	 * @param message info message {@link String}
	 */
	public static void sendInfo(@NotNull String message) {
		sendInfo(null, message);
	}

	/**
	 * Sends fine message to target
	 *
	 * @param target  target, if null sends to console
	 * @param message fine message {@link Component}
	 */
	public static void sendFine(
			@Nullable Object target,
			@NotNull Component message
	) {
		if (target instanceof Player player) {
			player.sendMessage(Badges.GREEN_EXCLAMATION_MARK.append(message.color(NamedTextColor.GREEN)));
		} else if (
				target instanceof CommandSender sender
				&& !(sender instanceof ConsoleCommandSender)
		) {
			sender.sendMessage(message.color(NamedTextColor.GREEN));
		} else {
			Bukkit.getLogger().info(serializeLegacyComponent(message.color(NamedTextColor.GREEN)));
		}
	}

	/**
	 * Sends fine message to console
	 *
	 * @param message fine message {@link Component}
	 */
	public static void sendFine(@NotNull Component message) {
		sendFine(null, message);
	}

	/**
	 * Sends fine message to target
	 *
	 * @param target  target, if null sends to console
	 * @param message fine message {@link String}
	 */
	public static void sendFine(
			@Nullable Object target,
			@NotNull String message
	) {
		sendFine(target, Component.text(message));
	}

	/**
	 * Sends fine message to console
	 *
	 * @param message fine message {@link String}
	 */
	public static void sendFine(@NotNull String message) {
		sendFine(null, message);
	}

	/**
	 * Sends warning message to target
	 *
	 * @param target  target, if null sends to console
	 * @param message warning message {@link Component}
	 */
	public static void sendWarning(
			@Nullable Object target,
			@NotNull Component message
	) {
		if (target instanceof Player player) {
			player.sendMessage(Badges.YELLOW_EXCLAMATION_MARK.append(message.color(NamedTextColor.GOLD)));
		} else if (
				target instanceof CommandSender sender
				&& !(sender instanceof ConsoleCommandSender)
		) {
			sender.sendMessage(message.color(NamedTextColor.GOLD));
		} else {
			Bukkit.getLogger().warning(serializeLegacyComponent(message.color(NamedTextColor.GOLD)));
		}
	}

	/**
	 * Sends warning message to console
	 *
	 * @param message warning message {@link Component}
	 */
	public static void sendWarning(@NotNull Component message) {
		sendWarning(null, message);
	}

	/**
	 * Sends warning message to target
	 *
	 * @param target  target, if null sends to console
	 * @param message warning message {@link String}
	 */
	public static void sendWarning(
			@Nullable Object target,
			@NotNull String message
	) {
		sendWarning(target, Component.text(message));
	}

	/**
	 * Sends warning message to console
	 *
	 * @param message warning message {@link String}
	 */
	public static void sendWarning(@NotNull String message) {
		sendWarning(null, message);
	}

	/**
	 * Sends error message to target
	 *
	 * @param target  target, if null sends to console
	 * @param message error message {@link Component}
	 */
	public static void sendError(
			@Nullable Object target,
			@NotNull Component message
	) {
		if (target instanceof Player player) {
			player.sendMessage(Badges.RED_EXCLAMATION_MARK.append(message.color(NamedTextColor.RED)));
		} else if (
				target instanceof CommandSender sender
				&& !(sender instanceof ConsoleCommandSender)
		) {
			sender.sendMessage(message.color(NamedTextColor.RED));
		} else {
			Bukkit.getLogger().severe(serializeLegacyComponent(message.color(NamedTextColor.RED)));
		}
	}

	/**
	 * Sends error message to console
	 *
	 * @param message error message {@link Component}
	 */
	public static void sendError(@NotNull Component message) {
		sendError(null, message);
	}

	/**
	 * Sends error message to target
	 *
	 * @param target  target, if null sends to console
	 * @param message error message {@link String}
	 */
	public static void sendError(
			@Nullable Object target,
			@NotNull String message
	) {
		sendError(target, Component.text(message));
	}

	/**
	 * Sends error message to console
	 *
	 * @param message error message {@link String}
	 */
	public static void sendError(@NotNull String message) {
		sendError(null, message);
	}

	@Contract("_, _ -> new")
	public static @NotNull String extractMessage(
			@NotNull String[] args,
			int start
	) {
		return String.join(" ", Arrays.copyOfRange(args, start, args.length));
	}

	@Contract("_ -> new")
	public static @NotNull Component createDefaultStyledText(@NotNull String text) {
		return Component.text().append(Component.text(text).style(DEFAULT_STYLE)).build();
	}

	@Contract("_ -> new")
	public static @NotNull String serializeGsonComponent(@NotNull Component component) {
		return GsonComponentSerializer.gson().serialize(component);
	}

	@Contract("_ -> new")
	public static @NotNull String serializeLegacyComponent(@NotNull Component component) {
		return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().serialize(component);
	}

	@Contract("_ -> new")
	public static @NotNull String serializePlainComponent(@NotNull Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}

	@Contract("_ -> new")
	public static @NotNull Component deserializeGsonComponent(@NotNull String text) {
		return GsonComponentSerializer.gson().deserialize(text);
	}

	@Contract("_ -> new")
	public static @NotNull Component deserializeLegacyComponent(@NotNull String text) {
		return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().deserialize(text);
	}

	@Contract("_ -> new")
	public static @NotNull Component deserializePlainComponent(@NotNull String text) {
		return PlainTextComponentSerializer.plainText().deserialize(text);
	}

	public static @Nullable List<Component> convertStringsToComponents(
			@Nullable Style style,
			String @NotNull [] strings
	) {
		List<Component> components = new ArrayList<>();
		for (String string : strings) {
			Component component = Component.text(string);
			components.add(
					style == null
					? component
					: component.style(style)
			);
		}
		return components.isEmpty() ? null : components;
	}

	public static @Nullable List<Component> convertStringsToComponents(
			@Nullable Style style,
			@NotNull String first,
			String @NotNull ... other
	) {
		String[] strings = new String[other.length + 1];
		strings[0] = first;
		System.arraycopy(other, 0, strings, 1, other.length);
		return convertStringsToComponents(style, strings);
	}

	public static @Nullable List<Component> convertStringsToComponents(String @NotNull [] strings) {
		return convertStringsToComponents(DEFAULT_STYLE, strings);
	}

	public static @Nullable List<Component> convertStringsToComponents(
			@NotNull String first,
			String @NotNull ... other
	) {
		return convertStringsToComponents(DEFAULT_STYLE, first, other);
	}
}
