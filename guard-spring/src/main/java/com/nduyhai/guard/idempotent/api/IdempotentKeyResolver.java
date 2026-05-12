package com.nduyhai.guard.idempotent.api;

import com.nduyhai.guard.annotation.Idempotent;
import com.nduyhai.guard.aop.GuardInvocationContext;

/**
 * SPI for computing the idempotency cache key from an invocation context.
 *
 * <p>The default implementation evaluates {@link Idempotent#key()} as a SpEL expression. Replace
 * this bean to implement key namespacing, hashing, or tenant-scoping.
 */
public interface IdempotentKeyResolver {

  /**
   * Resolves the idempotency key for the given context and annotation.
   *
   * @param context the current invocation context
   * @param annotation the {@code @Idempotent} annotation found on the method
   * @return a non-null, non-blank key string
   */
  String resolve(GuardInvocationContext context, Idempotent annotation);
}
