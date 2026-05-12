package com.nduyhai.guard.aop;

import org.jspecify.annotations.Nullable;

/**
 * Functional interface representing "the remainder of the execution chain" passed to each {@link
 * GuardHandler}. Calling {@link #invoke()} proceeds to the next handler or, at the end of the
 * chain, to the actual business method.
 */
@FunctionalInterface
public interface GuardOperationInvoker {

  /**
   * Proceeds with the next step in the guard execution chain.
   *
   * @return the return value of the guarded method (may be {@code null} for void methods)
   * @throws Throwable any exception propagated from downstream handlers or the method itself
   */
  @Nullable Object invoke() throws Throwable;
}
