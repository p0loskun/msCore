package com.github.minersstudios.mscore;

import com.github.minersstudios.mscore.tabcompleters.Empty;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface MSCommandExecutor extends CommandExecutor, TabCompleter {

	@Override
	default @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		return new Empty().onTabComplete(sender, command, label, args);
	}
}
