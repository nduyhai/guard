package com.nduyhai.guard.ratelimit.internal;

import com.nduyhai.guard.annotation.RateLimit;
import com.nduyhai.guard.aop.GuardHandler;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.aop.GuardOperationInvoker;
import com.nduyhai.guard.core.ratelimit.GuardRateLimiter;
import com.nduyhai.guard.core.ratelimit.RateLimitExceededException;
import com.nduyhai.guard.core.ratelimit.RateLimitOperation;
import com.nduyhai.guard.core.ratelimit.RateLimitResult;
import com.nduyhai.guard.ratelimit.api.RateLimitKeyResolver;
import com.nduyhai.guard.support.GuardUtils;
import org.jspecify.annotations.Nullable;

/**
 * {@link GuardHandler} that enforces the {@code @RateLimit} contract.
 *
 * <p>Throws {@link RateLimitExceededException} when the caller exceeds the configured limit.
 */
public final class RateLimitHandler implements GuardHandler {

  public static final int ORDER = 100;

  private final GuardRateLimiter rateLimiter;
  private final RateLimitKeyResolver keyResolver;

  public RateLimitHandler(GuardRateLimiter rateLimiter, RateLimitKeyResolver keyResolver) {
    this.rateLimiter = rateLimiter;
    this.keyResolver = keyResolver;
  }

  @Override
  @Nullable
  public Object handle(GuardInvocationContext context, GuardOperationInvoker invoker)
      throws Throwable {
    RateLimit annotation = context.getAnnotation(RateLimit.class);
    if (annotation == null) {
      return invoker.invoke();
    }

    String key = keyResolver.resolve(context, annotation);
    RateLimitOperation operation =
        new RateLimitOperation(
            key, annotation.limit(), GuardUtils.parseDuration(annotation.window()));

    RateLimitResult result = rateLimiter.tryAcquire(operation);
    if (!result.allowed()) {
      throw new RateLimitExceededException(key, result.retryAfter());
    }

    return invoker.invoke();
  }

  @Override
  public boolean supports(GuardInvocationContext context) {
    return context.hasAnnotation(RateLimit.class);
  }

  @Override
  public int getOrder() {
    return ORDER;
  }
}
