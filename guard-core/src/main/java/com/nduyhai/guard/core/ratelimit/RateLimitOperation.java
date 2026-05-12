package com.nduyhai.guard.core.ratelimit;

import java.time.Duration;

/**
 * Resolved metadata for a rate-limit guard operation.
 *
 * @param key    the resolved rate-limit bucket key (after SpEL evaluation)
 * @param limit  maximum number of calls permitted within {@code window}
 * @param window the sliding time window
 */
public record RateLimitOperation(String key, long limit, Duration window) {

    public RateLimitOperation {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("RateLimit key must not be blank");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("RateLimit limit must be positive");
        }
        if (window == null || window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("RateLimit window must be positive");
        }
    }
}
