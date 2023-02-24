package com.github.minersstudios.mscore.collections;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class ConcurrentDualMap<K1, K2, V> {
	private final @NotNull ConcurrentHashMap<K1, Map.Entry<K2, V>> map = new ConcurrentHashMap<>();
	private final @NotNull ConcurrentHashMap<K2, K1> keyMap = new ConcurrentHashMap<>();

	public @Nullable V put(@NotNull K1 key1, @NotNull K2 key2, @NotNull V value) {
		this.keyMap.put(key2, key1);
		Map.Entry<K2, V> entry = new AbstractMap.SimpleEntry<>(key2, value);
		return this.map.put(key1, entry) != null ? entry.getValue() : null;
	}

	@Contract(pure = true)
	public @NotNull Set<K1> primaryKeySet() {
		return this.map.keySet();
	}

	@Contract(pure = true)
	public @NotNull Set<K2> secondaryKeySet() {
		return this.keyMap.keySet();
	}

	@Contract(pure = true)
	public @NotNull Collection<V> values() {
		return this.map.values().stream()
				.map(Map.Entry::getValue)
				.collect(Collectors.toList());
	}

	public @Nullable K1 getPrimaryKey(@Nullable K2 key2) {
		return this.keyMap.get(key2);
	}

	public @Nullable K2 getSecondaryKey(@Nullable K1 key1) {
		Map.Entry<K2, V> entry = this.map.get(key1);
		return entry != null ? entry.getKey() : null;
	}

	public @Nullable V getByPrimaryKey(@Nullable K1 key1) {
		Map.Entry<K2, V> entry = this.map.get(key1);
		return entry != null ? entry.getValue() : null;
	}

	public @Nullable V getBySecondaryKey(@Nullable K2 key2) {
		return this.getByPrimaryKey(this.keyMap.get(key2));
	}

	public boolean containsPrimaryKey(@Nullable K1 key1) {
		return this.map.containsKey(key1);
	}

	public boolean containsSecondaryKey(@Nullable K2 key2) {
		return this.secondaryKeySet().contains(key2);
	}

	public boolean containsValue(@Nullable V value) {
		return this.values().contains(value);
	}

	public @Nullable V removeByPrimaryKey(@NotNull K1 key1) {
		Map.Entry<K2, V> entry = this.map.remove(key1);
		this.keyMap.remove(entry.getKey());
		return entry.getValue();
	}

	public @Nullable V removeBySecondaryKey(@NotNull K2 key2) {
		return this.map.remove(this.keyMap.remove(key2)).getValue();
	}

	public void clear() {
		this.map.clear();
		this.keyMap.clear();
	}

	public int size() {
		return this.map.size();
	}

	public boolean isEmpty() {
		return this.map.isEmpty();
	}
}
