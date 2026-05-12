package com.nduyhai.guard.core.idempotent;

import java.time.Duration;

/**
 * Resolved metadata for an idempotency guard operation.
 *
 * @param key the resolved cache key (after SpEL evaluation)
 * @param ttl time-to-live for the cached result
 */
public record IdempotentOperation(String key, Duration ttl) {

    public IdempotentOperation {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Idempotent key must not be blank");
        }
        if (ttl == null || ttl.isNegative()) {
            throw new IllegalArgumentException("Idempotent ttl must be non-negative");
        }
    }
}
