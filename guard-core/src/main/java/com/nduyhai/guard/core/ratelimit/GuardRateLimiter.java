package com.nduyhai.guard.core.ratelimit;

/**
 * SPI for rate-limiting call attempts.
 *
 * <p>Provided built-ins:
 * <ul>
 *   <li>{@code SlidingWindowRateLimiter} — in-memory sliding-window algorithm</li>
 * </ul>
 *
 * <p>Implementations must be thread-safe.
 */
public interface GuardRateLimiter {

    /**
     * Attempts to acquire a token from the rate-limit bucket described by {@code operation}.
     *
     * @return a {@link RateLimitResult} indicating whether the call is permitted
     */
    RateLimitResult tryAcquire(RateLimitOperation operation);
}
