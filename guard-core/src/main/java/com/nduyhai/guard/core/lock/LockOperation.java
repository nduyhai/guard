package com.nduyhai.guard.core.lock;

import java.time.Duration;

/**
 * Resolved metadata for a distributed-lock guard operation.
 *
 * @param key       the resolved lock key (after SpEL evaluation)
 * @param waitTime  maximum time to wait when acquiring the lock
 * @param leaseTime how long the lock is held before automatic expiry
 */
public record LockOperation(String key, Duration waitTime, Duration leaseTime) {

    public LockOperation {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Lock key must not be blank");
        }
        if (waitTime == null || waitTime.isNegative()) {
            throw new IllegalArgumentException("Lock waitTime must be non-negative");
        }
        if (leaseTime == null || leaseTime.isNegative()) {
            throw new IllegalArgumentException("Lock leaseTime must be non-negative");
        }
    }
}
