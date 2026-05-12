package com.nduyhai.guard.lock.internal;

import com.nduyhai.guard.core.lock.LockHandle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/** {@link LockHandle} backed by a JVM {@link ReentrantLock}. */
final class ReentrantLockHandle implements LockHandle {

  private final ReentrantLock lock;
  private final String key;
  private final AtomicBoolean released = new AtomicBoolean(false);

  ReentrantLockHandle(ReentrantLock lock, String key) {
    this.lock = lock;
    this.key = key;
  }

  @Override
  public void release() {
    if (released.compareAndSet(false, true)) {
      lock.unlock();
    }
  }

  @Override
  public boolean isHeld() {
    return !released.get() && lock.isHeldByCurrentThread();
  }

  @Override
  public String toString() {
    return "ReentrantLockHandle[key=" + key + ", held=" + isHeld() + "]";
  }
}
