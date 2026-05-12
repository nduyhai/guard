package com.nduyhai.guard.ratelimit.api;

import com.nduyhai.guard.annotation.RateLimit;
import com.nduyhai.guard.aop.GuardInvocationContext;

/**
 * SPI for resolving the rate-limit bucket key from an invocation context.
 *
 * <p>The default implementation evaluates {@link RateLimit#key()} as SpEL.
 */
public interface RateLimitKeyResolver {

  /**
   * Resolves the rate-limit bucket key for the given context and annotation.
   *
   * @return a non-null, non-blank key string
   */
  String resolve(GuardInvocationContext context, RateLimit annotation);
}
