package com.nduyhai.guard.lock.internal;

import com.nduyhai.guard.annotation.DistributedLock;
import com.nduyhai.guard.aop.GuardHandler;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.aop.GuardOperationInvoker;
import com.nduyhai.guard.core.lock.LockHandle;
import com.nduyhai.guard.core.lock.LockOperation;
import com.nduyhai.guard.core.lock.LockProvider;
import com.nduyhai.guard.lock.api.LockKeyResolver;
import com.nduyhai.guard.support.GuardUtils;
import org.jspecify.annotations.Nullable;

/**
 * {@link GuardHandler} that acquires a distributed lock before invoking the method and releases it
 * unconditionally in a finally block.
 */
public final class DistributedLockHandler implements GuardHandler {

  public static final int ORDER = 300;

  private final LockProvider lockProvider;
  private final LockKeyResolver keyResolver;

  public DistributedLockHandler(LockProvider lockProvider, LockKeyResolver keyResolver) {
    this.lockProvider = lockProvider;
    this.keyResolver = keyResolver;
  }

  @Override
  @Nullable
  public Object handle(GuardInvocationContext context, GuardOperationInvoker invoker)
      throws Throwable {
    DistributedLock annotation = context.getAnnotation(DistributedLock.class);
    if (annotation == null) {
      return invoker.invoke();
    }

    String key = keyResolver.resolve(context, annotation);
    LockOperation operation =
        new LockOperation(
            key,
            GuardUtils.parseDuration(annotation.waitTime()),
            GuardUtils.parseDuration(annotation.leaseTime()));

    try (LockHandle handle = lockProvider.acquire(operation)) {
      return invoker.invoke();
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
