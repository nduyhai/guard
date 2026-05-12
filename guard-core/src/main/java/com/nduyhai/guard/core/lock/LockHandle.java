package com.nduyhai.guard.core.lock;

/**
 * A handle to a successfully acquired distributed lock.
 *
 * <p>Implementations must be safe to use with try-with-resources; {@link #close()}
 * delegates to {@link #release()}.
 */
public interface LockHandle extends AutoCloseable {

    /** Releases the lock. Idempotent — safe to call multiple times. */
    void release();

    /** Returns {@code true} if the lock is still held by this handle. */
    boolean isHeld();

    @Override
    default void close() {
        release();
    }
}
