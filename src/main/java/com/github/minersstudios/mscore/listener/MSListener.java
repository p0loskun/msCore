package com.github.minersstudios.mscore.listener;

import org.bukkit.event.Listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Loads all listeners in the project that are annotated with this interface
 * <p>
 * All listeners must be implemented using {@link Listener}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MSListener {}
