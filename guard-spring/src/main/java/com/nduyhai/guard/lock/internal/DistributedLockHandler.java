package com.nduyhai.guard.lock.internal;

import com.nduyhai.guard.annotation.DistributedLock;
import com.nduyhai.guard.aop.GuardHandler;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.aop.GuardOperationInvoker;
import com.nduyhai.guard.core.lock.LockException;
import com.nduyhai.guard.core.lock.LockHandle;
import com.nduyhai.guard.core.lock.LockOperation;
import com.nduyhai.guard.core.lock.LockProvider;
import com.nduyhai.guard.lock.api.LockKeyResolver;
import com.nduyhai.guard.metrics.GuardMetrics;
import com.nduyhai.guard.support.GuardUtils;
import org.jspecify.annotations.Nullable;

/**
 * {@link GuardHandler} that acquires a distributed lock before invoking the method and releases it
 * unconditionally in a finally block.
 *
 * <p>Records the following metrics via {@link GuardMetrics}:
 *
 * <ul>
 *   <li>{@code guard.lock.acquired} — lock obtained successfully
 *   <li>{@code guard.lock.failed} — lock could not be acquired ({@link LockException})
 *   <li>{@code guard.lock.released} — lock released after method execution (success or failure)
 * </ul>
 */
public final class DistributedLockHandler implements GuardHandler {

  public static final int ORDER = 300;

  private final LockProvider lockProvider;
  private final LockKeyResolver keyResolver;
  private final GuardMetrics metrics;

  public DistributedLockHandler(
      LockProvider lockProvider, LockKeyResolver keyResolver, GuardMetrics metrics) {
    this.lockProvider = lockProvider;
    this.keyResolver = keyResolver;
    this.metrics = metrics;
  }

  @Override
  @Nullable
  public Object handle(GuardInvocationContext context, GuardOperationInvoker invoker)
      throws Throwable {
    DistributedLock annotation = context.getAnnotation(DistributedLock.class);
    if (annotation == null) {
      return invoker.invoke();
    }

    String className = context.getTargetClass().getSimpleName();
    String methodName = context.getMethod().getName();
    String provider = lockProvider.getClass().getSimpleName();

    String key = keyResolver.resolve(context, annotation);
    LockOperation operation =
        new LockOperation(
            key,
            GuardUtils.parseDuration(annotation.waitTime()),
            GuardUtils.parseDuration(annotation.leaseTime()));

    LockHandle handle;
    try {
      handle = lockProvider.acquire(operation);
    } catch (LockException ex) {
      metrics.recordLockFailed(className, methodName, provider);
      throw ex;
    }

    metrics.recordLockAcquired(className, methodName, provider);
    try (handle) {
      return invoker.invoke();
    } finally {
      metrics.recordLockReleased(className, methodName, provider);
    }
  }

  @Override
  public boolean supports(GuardInvocationContext context) {
    return context.hasAnnotation(DistributedLock.class);
  }

  @Override
  public int getOrder() {
    return ORDER;
  }
}
