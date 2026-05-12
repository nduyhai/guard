package com.nduyhai.guard.core.ratelimit;

import com.nduyhai.guard.core.GuardException;

import java.time.Duration;

/**
 * Thrown when a caller exceeds the configured rate limit for a guarded method.
 */
public class RateLimitExceededException extends GuardException {

    private final Duration retryAfter;

    public RateLimitExceededException(String key, Duration retryAfter) {
        super("Rate limit exceeded for key '" + key + "'. Retry after " + retryAfter);
        this.retryAfter = retryAfter;
    }

    /** Suggested duration the caller should wait before retrying. */
    public Duration getRetryAfter() {
        return retryAfter;
    }
}
