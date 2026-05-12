package com.nduyhai.guard.ratelimit.internal;

import com.nduyhai.guard.core.ratelimit.GuardRateLimiter;
import com.nduyhai.guard.core.ratelimit.RateLimitOperation;
import com.nduyhai.guard.core.ratelimit.RateLimitResult;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In-memory sliding-window {@link GuardRateLimiter}.
 *
 * <p>Maintains a per-key deque of call timestamps. On each {@link #tryAcquire} call:
 * <ol>
 *   <li>Timestamps older than the window start are discarded.</li>
 *   <li>If the count is below the limit, a new timestamp is recorded and the call is allowed.</li>
 *   <li>Otherwise the call is denied with a {@code retryAfter} hint.</li>
 * </ol>
 *
 * <p>A per-key {@link ReentrantLock} serialises access to each deque, making the
 * implementation virtual-thread friendly (no {@code synchronized} on shared monitors).
 */
public final class SlidingWindowRateLimiter implements GuardRateLimiter {

    private final ConcurrentHashMap<String, BucketState> buckets = new ConcurrentHashMap<>();

    @Override
    public RateLimitResult tryAcquire(RateLimitOperation operation) {
        BucketState state = buckets.computeIfAbsent(operation.key(), k -> new BucketState());
        state.lock().lock();
        try {
            return evaluate(state.timestamps(), operation);
        } finally {
            state.lock().unlock();
        }
    }

    private RateLimitResult evaluate(Deque<Instant> timestamps, RateLimitOperation operation) {
        Instant now = Instant.now();
        Instant windowStart = now.minus(operation.window());

        // Evict expired entries
        while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(windowStart)) {
            timestamps.pollFirst();
        }

        long count = timestamps.size();
        if (count >= operation.limit()) {
            Instant oldest = timestamps.peekFirst();
            Duration retryAfter = (oldest != null)
                    ? Duration.between(now, oldest.plus(operation.window()))
                    : operation.window();
            return RateLimitResult.denied(retryAfter.isNegative() ? Duration.ZERO : retryAfter);
        }

        timestamps.addLast(now);
        return RateLimitResult.allowed(operation.limit() - timestamps.size());
    }

    private record BucketState(Deque<Instant> timestamps, ReentrantLock lock) {
        BucketState() {
            this(new ArrayDeque<>(), new ReentrantLock());
        }
    }
}
