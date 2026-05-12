package com.nduyhai.guard.aop;

import org.jspecify.annotations.Nullable;

/**
 * SPI for pluggable guard behaviour executed around a method invocation.
 *
 * <p>Implementations form a chain-of-responsibility, each deciding whether to short-circuit or
 * delegate to the next handler via {@code invoker.invoke()}.
 *
 * <h2>Execution order</h2>
 *
 * Handlers are sorted by {@link #getOrder()} ascending; lower values execute first (outermost
 * wrapper). The recommended order constants are:
 *
 * <ul>
 *   <li>AuditLogHandler — {@code 50} (outermost, captures result post-execution)
 *   <li>RateLimitHandler — {@code 100}
 *   <li>IdempotentHandler — {@code 200}
 *   <li>DistributedLockHandler — {@code 300} (innermost, directly wraps the method)
 * </ul>
 */
public interface GuardHandler {

  /**
   * Executes this handler's logic, optionally delegating to {@code invoker}.
   *
   * @param context the invocation context for the current method call
   * @param invoker callback to proceed to the next handler or the guarded method
   * @return the (possibly intercepted) return value
   * @throws Throwable any exception from this handler or downstream
   */
  @Nullable Object handle(GuardInvocationContext context, GuardOperationInvoker invoker)
      throws Throwable;

  /**
   * Returns {@code true} if this handler applies to {@code context}. The chain skips handlers that
   * return {@code false}.
   */
  boolean supports(GuardInvocationContext context);

  /** Execution order — lower value = earlier (outermost) in the chain. */
  int getOrder();
}
