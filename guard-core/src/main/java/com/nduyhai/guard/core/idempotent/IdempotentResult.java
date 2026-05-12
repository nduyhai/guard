package com.nduyhai.guard.core.idempotent;

/**
 * Outcome of an idempotency check.
 *
 * @param value  the cached or freshly computed return value
 * @param cached {@code true} if the value was served from the idempotency store
 */
public record IdempotentResult(Object value, boolean cached) {

    public static IdempotentResult fromCache(Object value) {
        return new IdempotentResult(value, true);
    }

    public static IdempotentResult fresh(Object value) {
        return new IdempotentResult(value, false);
    }
}
