package com.nduyhai.guard.lock.internal;

import com.nduyhai.guard.core.lock.LockException;
import com.nduyhai.guard.core.lock.LockHandle;
import com.nduyhai.guard.core.lock.LockOperation;
import com.nduyhai.guard.core.lock.LockProvider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In-memory {@link LockProvider} using a per-key {@link ReentrantLock}.
 *
 * <p>Suitable for single-node deployments and testing. Replace with {@code RedisLockProvider} for
 * distributed scenarios.
 *
 * <p>Virtual-thread friendly: uses {@link ReentrantLock#tryLock(long, TimeUnit)} which parks the
 * carrier thread rather than blocking it.
 */
public final class InMemoryLockProvider implements LockProvider {

  private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

  @Override
  public LockHandle acquire(LockOperation operation) {
    ReentrantLock lock = locks.computeIfAbsent(operation.key(), k -> new ReentrantLock());
    boolean acquired;
    try {
      acquired = lock.tryLock(operation.waitTime().toMillis(), TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new LockException("Interrupted while acquiring lock for key: " + operation.key(), e);
    }
    if (!acquired) {
      throw new LockException(
          "Could not acquire lock for key '"
              + operation.key()
              + "' within "
              + operation.waitTime());
    }
    return new ReentrantLockHandle(lock, operation.key());
  }
}
