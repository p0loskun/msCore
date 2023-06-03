package com.github.minersstudios.mscore.utils;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;

import java.util.List;

import static com.github.minersstudios.mscore.utils.ChatUtils.createDefaultStyledText;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("unused")
public final class Badges {
	public static final List<Component> PAINTABLE_LORE_COMPONENT = Lists.newArrayList(createDefaultStyledText("ꀢ"));
	public static final List<Component> WRENCHABLE_LORE_COMPONENT = Lists.newArrayList(createDefaultStyledText("ꀳ"));

	public static final Component
			GREEN_EXCLAMATION_MARK = text(" ꀒ "),
			YELLOW_EXCLAMATION_MARK = text(" ꀓ "),
			RED_EXCLAMATION_MARK = text(" ꀑ "),
			SPEECH = text(" ꀕ "),
			DISCORD = text(" ꀔ ");

	@Contract(" -> fail")
	private Badges() {
		throw new IllegalStateException("Utility class");
	}
}
