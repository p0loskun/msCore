package com.github.minersstudios.mscore.utils;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;

import java.util.List;

public final class Badges {
	public static final List<String> PAINTABLE_LORE = Lists.newArrayList(ChatColor.WHITE + "ꀢ");
	public static final List<Component> PAINTABLE_LORE_COMPONENT = Lists.newArrayList(ChatUtils.createDefaultStyledText("ꀢ"));

	public static final Component
			GREEN_EXCLAMATION_MARK = Component.text(" ꀒ "),
			YELLOW_EXCLAMATION_MARK = Component.text(" ꀓ "),
			RED_EXCLAMATION_MARK = Component.text(" ꀑ "),
			SPEECH = Component.text(" ꀕ "),
			DISCORD = Component.text(" ꀔ ");

	private Badges() {
		throw new IllegalStateException("Utility class");
	}
}
