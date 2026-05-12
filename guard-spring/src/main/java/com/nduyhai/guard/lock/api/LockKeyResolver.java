package com.nduyhai.guard.lock.api;

import com.nduyhai.guard.annotation.DistributedLock;
import com.nduyhai.guard.aop.GuardInvocationContext;

/**
 * SPI for resolving the distributed-lock key from an invocation context.
 *
 * <p>The default implementation evaluates {@link DistributedLock#key()} as SpEL.
 */
public interface LockKeyResolver {

  /**
   * Resolves the lock key for the given context and annotation.
   *
   * @return a non-null, non-blank lock key string
   */
  String resolve(GuardInvocationContext context, DistributedLock annotation);
}
