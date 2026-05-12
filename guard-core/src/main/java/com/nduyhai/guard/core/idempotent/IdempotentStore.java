package com.nduyhai.guard.core.idempotent;

import java.time.Duration;
import java.util.Optional;

/**
 * SPI for persisting idempotent method results.
 *
 * <p>Implementations must be thread-safe. Provided built-ins:
 * <ul>
 *   <li>{@code InMemoryIdempotentStore} — for single-node / testing scenarios</li>
 *   <li>{@code RedisIdempotentStore}    — for distributed deployments</li>
 * </ul>
 */
public interface IdempotentStore {

    /**
     * Returns the cached result for {@code key}, or empty if absent or expired.
     */
    Optional<Object> get(String key);

    /**
     * Stores {@code result} under {@code key} with the given {@code ttl}.
     * Overwrites any existing entry.
     */
    void put(String key, Object result, Duration ttl);

    /**
     * Returns {@code true} if a non-expired entry exists for {@code key}.
     */
    default boolean contains(String key) {
        return get(key).isPresent();
    }
}
