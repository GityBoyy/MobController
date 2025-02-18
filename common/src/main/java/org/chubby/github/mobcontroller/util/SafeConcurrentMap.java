package org.chubby.github.mobcontroller.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A thread-safe map implementation that automatically handles null checks for keys and values.
 * Wraps ConcurrentHashMap with additional safety features.
 */
public class SafeConcurrentMap<K, V> {
    private final ConcurrentHashMap<K, V> internalMap;

    public SafeConcurrentMap() {
        this.internalMap = new ConcurrentHashMap<>();
    }

    /**
     * Safely puts a value into the map.
     * @return true if the value was successfully put, false if either key or value was null
     */
    public boolean put(@Nullable K key, @Nullable V value) {
        if (key == null || value == null) {
            return false;
        }
        internalMap.put(key, value);
        return true;
    }

    /**
     * Safely gets a value from the map.
     * @return Optional containing the value, or empty if the key was null or not found
     */
    public Optional<V> get(@Nullable K key) {
        if (key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(internalMap.get(key));
    }

    /**
     * Safely removes a key-value pair from the map.
     * @return Optional containing the removed value, or empty if the key was null or not found
     */
    public Optional<V> remove(@Nullable K key) {
        if (key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(internalMap.remove(key));
    }

    /**
     * Safely checks if a key exists in the map.
     */
    public boolean containsKey(@Nullable K key) {
        return key != null && internalMap.containsKey(key);
    }

    /**
     * Safely checks if a value exists in the map.
     */
    public boolean containsValue(@Nullable V value) {
        return value != null && internalMap.containsValue(value);
    }

    /**
     * Safely computes a value if absent.
     * @return Optional containing the computed or existing value, or empty if the key or mapping function was null
     */
    public Optional<V> computeIfAbsent(@Nullable K key, @Nullable Function<? super K, ? extends V> mappingFunction) {
        if (key == null || mappingFunction == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(internalMap.computeIfAbsent(key, mappingFunction));
    }

    /**
     * Safely replaces an existing value.
     * @return Optional containing the old value, or empty if the key or new value was null
     */
    public Optional<V> replace(@Nullable K key, @Nullable V value) {
        if (key == null || value == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(internalMap.replace(key, value));
    }

    /**
     * Gets the underlying map. Use with caution as it bypasses null safety checks.
     */
    @NotNull
    public Map<K, V> getInternalMap() {
        return internalMap;
    }

    /**
     * Clears all entries from the map.
     */
    public void clear() {
        internalMap.clear();
    }

    /**
     * Gets the size of the map.
     */
    public int size() {
        return internalMap.size();
    }

    /**
     * Checks if the map is empty.
     */
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }
}