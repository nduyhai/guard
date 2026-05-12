package com.nduyhai.guard.core.lock;

import com.nduyhai.guard.core.GuardException;

/**
 * Thrown when a distributed lock cannot be acquired within the configured wait time,
 * or when the lock provider encounters an infrastructure error.
 */
public class LockException extends GuardException {

    public LockException(String message) {
        super(message);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }
}
