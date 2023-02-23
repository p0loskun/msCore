package com.github.minersstudios.mscore.collections;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public final class DualMap<K1, K2, V> {
	private final @NotNull HashMap<K1, V> primaryMap = new HashMap<>();
	private final @NotNull HashMap<K2, K1> secondaryKeyMap = new HashMap<>();

	public V put(@NotNull K1 key1, @NotNull K2 key2, @NotNull V value) {
		this.secondaryKeyMap.put(key2, key1);
		return this.primaryMap.put(key1, value);
	}

	@Contract(pure = true)
	public @NotNull Set<K1> primaryKeySet() {
		return this.primaryMap.keySet();
	}

	@Contract(pure = true)
	public @NotNull Set<K2> secondaryKeySet() {
		return this.secondaryKeyMap.keySet();
	}

	@Contract(pure = true)
	public @NotNull Collection<V> values() {
		return this.primaryMap.values();
	}

	public @NotNull K1 getPrimaryKey(@NotNull K2 key2) {
		return this.secondaryKeyMap.get(key2);
	}

	public @NotNull K2 getSecondaryKey(@NotNull K1 key1) {
		for (Map.Entry<K2, K1> entry : this.secondaryKeyMap.entrySet()) {
			if (entry.getValue().equals(key1)) {
				return entry.getKey();
			}
		}
		throw new NullPointerException();
	}

	public @Nullable V getByPrimaryKey(@Nullable K1 key1) {
		return this.primaryMap.get(key1);
	}

	public @Nullable V getBySecondaryKey(@Nullable K2 key2) {
		K1 key1 = this.secondaryKeyMap.get(key2);
		return this.getByPrimaryKey(key1);
	}

	public boolean containsPrimaryKey(@Nullable K1 key) {
		return this.primaryMap.containsKey(key);
	}

	public boolean containsSecondaryKey(@Nullable K2 key) {
		return this.secondaryKeyMap.containsKey(key);
	}

	public boolean containsValue(@Nullable V value) {
		return this.primaryMap.containsValue(value);
	}

	public @NotNull V removeByPrimaryKey(@NotNull K1 key) {
		V removed = this.primaryMap.remove(key);
		this.secondaryKeyMap.remove(this.getSecondaryKey(key));
		return removed;
	}

	public @NotNull V removeBySecondaryKey(@NotNull K2 key2) {
		return this.removeByPrimaryKey(this.getPrimaryKey(key2));
	}

	public void clear() {
		this.primaryMap.clear();
		this.secondaryKeyMap.clear();
	}

	public int size() {
		return this.primaryMap.size();
	}

	public boolean isEmpty() {
		return this.primaryMap.isEmpty();
	}
}
