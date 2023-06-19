package com.github.minersstudios.mscore.command;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public final class Commodore {
    public final List<Command> commands = new ArrayList<>();

    private static final Field CHILDREN_FIELD;
    private static final Field LITERALS_FIELD;
    private static final Field ARGUMENTS_FIELD;
    private static final Field CUSTOM_SUGGESTIONS_FIELD;
    private static final Field COMMAND_EXECUTE_FUNCTION_FIELD;

    private static final com.mojang.brigadier.Command<?> COMMAND;
    private static final SuggestionProvider<?> SUGGESTION_PROVIDER;

    static {
        try {
            CHILDREN_FIELD = CommandNode.class.getDeclaredField("children");
            LITERALS_FIELD = CommandNode.class.getDeclaredField("literals");
            ARGUMENTS_FIELD = CommandNode.class.getDeclaredField("arguments");
            CUSTOM_SUGGESTIONS_FIELD = ArgumentCommandNode.class.getDeclaredField("customSuggestions");
            COMMAND_EXECUTE_FUNCTION_FIELD = CommandNode.class.getDeclaredField("command");

            CHILDREN_FIELD.setAccessible(true);
            LITERALS_FIELD.setAccessible(true);
            ARGUMENTS_FIELD.setAccessible(true);
            CUSTOM_SUGGESTIONS_FIELD.setAccessible(true);
            COMMAND_EXECUTE_FUNCTION_FIELD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }

        COMMAND = (context) -> {
            throw new UnsupportedOperationException();
        };
        SUGGESTION_PROVIDER = (context, builder) -> {
            throw new UnsupportedOperationException();
        };
    }

    public Commodore(@NotNull Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @SuppressWarnings({"UnstableApiUsage"})
            @EventHandler
            public void onPlayerSendCommandsEvent(@NotNull AsyncPlayerSendCommandsEvent<?> event) {
                if (event.isAsynchronous() || !event.hasFiredAsync()) {
                    Player player = event.getPlayer();
                    RootCommandNode<?> commandNode = event.getCommandNode();

                    for (Command command : Commodore.this.commands) {
                        command.apply(player, commandNode);
                    }
                }
            }
        }, plugin);
    }

    @SuppressWarnings("unchecked")
    public void register(
            @NotNull PluginCommand command,
            @NotNull LiteralCommandNode<?> node,
            @NotNull Predicate<? super Player> permissionTest
    ) {
        try {
            setFields(node, SUGGESTION_PROVIDER);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Collection<String> aliases = getAliases(command);

        if (!aliases.contains(node.getLiteral())) {
            node = renameLiteralNode(node, command.getName());
        }

        for (String alias : aliases) {
            if (node.getLiteral().equals(alias)) {
                this.commands.add(new Command(node, permissionTest));
            } else {
                this.commands.add(new Command(
                        literal(alias)
                                .redirect((CommandNode<Object>) node)
                                .build(),
                        permissionTest
                ));
            }
        }
    }

    public void register(
            @NotNull PluginCommand command,
            @NotNull LiteralCommandNode<?> argumentBuilder
    ) {
        register(command, argumentBuilder, command::testPermissionSilent);
    }

    private static void removeChild(
            @NotNull RootCommandNode<?> root,
            @NotNull String name
    ) {
        try {
            ((Map<?, ?>) CHILDREN_FIELD.get(root)).remove(name);
            ((Map<?, ?>) LITERALS_FIELD.get(root)).remove(name);
            ((Map<?, ?>) ARGUMENTS_FIELD.get(root)).remove(name);
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    private static void setFields(
            @NotNull CommandNode<?> node,
            @Nullable SuggestionProvider<?> suggestionProvider
    ) throws IllegalAccessException {
        COMMAND_EXECUTE_FUNCTION_FIELD.set(node, COMMAND);

        if (
                suggestionProvider != null
                && node instanceof ArgumentCommandNode<?, ?> argumentNode
        ) {
            CUSTOM_SUGGESTIONS_FIELD.set(argumentNode, suggestionProvider);
        }

        for (CommandNode<?> child : node.getChildren()) {
            setFields(child, suggestionProvider);
        }
    }

    private static <S> @NotNull LiteralCommandNode<S> renameLiteralNode(
            @NotNull LiteralCommandNode<S> node,
            @NotNull String literal
    ) {
        LiteralCommandNode<S> clone = new LiteralCommandNode<>(
                literal,
                node.getCommand(),
                node.getRequirement(),
                node.getRedirect(),
                node.getRedirectModifier(),
                node.isFork()
        );

        for (CommandNode<S> child : node.getChildren()) {
            clone.addChild(child);
        }
        return clone;
    }

    private static Collection<String> getAliases(@NotNull PluginCommand command) {
        Stream<String> aliasesStream = Stream.concat(
                Stream.of(command.getLabel()),
                command.getAliases().stream()
        );
        String pluginName = command.getPlugin().getName().toLowerCase().trim();
        aliasesStream = aliasesStream.flatMap(
                alias -> Stream.of(alias, pluginName + ":" + alias)
        );
        return aliasesStream.distinct().collect(Collectors.toList());
    }

    private record Command(
            @NotNull CommandNode<?> node,
            @NotNull Predicate<? super Player> permissionTest
    ) {

        @SuppressWarnings({"unchecked", "rawtypes"})
        public void apply(
                @NotNull Player player,
                @NotNull RootCommandNode<?> root
        ) {
            if (!this.permissionTest.test(player)) return;
            removeChild(root, this.node.getName());
            root.addChild((CommandNode) this.node);
        }
    }
}
