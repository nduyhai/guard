package com.nduyhai.guard.core.lock;

/**
 * SPI for acquiring distributed locks.
 *
 * <p>Provided built-ins:
 * <ul>
 *   <li>{@code InMemoryLockProvider} — JVM-local {@link java.util.concurrent.locks.ReentrantLock}</li>
 *   <li>{@code RedisLockProvider}    — Redis-backed via {@code SET NX PX}</li>
 * </ul>
 */
public interface LockProvider {

    /**
     * Attempts to acquire the lock described by {@code operation}.
     *
     * @return a {@link LockHandle} that must be released after use
     * @throws LockException if the lock cannot be acquired within {@code waitTime}
     */
    LockHandle acquire(LockOperation operation);
}
