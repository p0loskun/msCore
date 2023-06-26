package com.github.minersstudios.mscore.utils;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("unused")
public final class Badges {
    public static final Component GREEN_EXCLAMATION_MARK = text(" ꀒ ");
    public static final Component YELLOW_EXCLAMATION_MARK = text(" ꀓ ");
    public static final Component RED_EXCLAMATION_MARK = text(" ꀑ ");
    public static final Component SPEECH = text(" ꀕ ");
    public static final Component DISCORD = text(" ꀔ ");
    public static final Component PAINTABLE_LORE = text("ꀢ");
    public static final Component WRENCHABLE_LORE = text("ꀳ");

    public static final ImmutableList<Component> PAINTABLE_LORE_LIST = ImmutableList.of(PAINTABLE_LORE);
    public static final ImmutableList<Component> WRENCHABLE_LORE_LIST = ImmutableList.of(WRENCHABLE_LORE);

    @Contract(" -> fail")
    private Badges() {
        throw new AssertionError("Utility class");
    }
}
