package com.nduyhai.guard.core.ratelimit;

import java.time.Duration;

/**
 * Outcome of a rate-limit token acquisition attempt.
 *
 * @param allowed    whether the call is permitted
 * @param remaining  number of remaining tokens in the current window
 * @param retryAfter suggested wait before the next allowed attempt (zero when {@code allowed})
 */
public record RateLimitResult(boolean allowed, long remaining, Duration retryAfter) {

    public static RateLimitResult allowed(long remaining) {
        return new RateLimitResult(true, remaining, Duration.ZERO);
    }

    public static RateLimitResult denied(Duration retryAfter) {
        return new RateLimitResult(false, 0, retryAfter);
    }
}
