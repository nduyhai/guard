package com.nduyhai.guard.core.idempotent;

import com.nduyhai.guard.core.GuardException;

/**
 * Thrown when the idempotency subsystem encounters an unrecoverable error
 * (e.g. store connectivity failure). Distinct from a cache miss, which is handled silently.
 */
public class IdempotentException extends GuardException {

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
    }
}
