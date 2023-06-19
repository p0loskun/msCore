package com.github.minersstudios.mscore.command;

import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers all commands in the project that are annotated with this interface
 * <p>
 * All commands must be implemented using {@link MSCommandExecutor} and located in the "com.github.minersstudios.plugin-name.commands" folder
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MSCommand {

    @NotNull String command();

    @NotNull String usage() default "";

    @NotNull String description() default "";

    String @NotNull [] aliases() default {};

    @NotNull String permission() default "";

    @NotNull PermissionDefault permissionDefault() default PermissionDefault.NOT_OP;

    String @NotNull [] permissionParentKeys() default {};

    boolean[] permissionParentValues() default {};
}
