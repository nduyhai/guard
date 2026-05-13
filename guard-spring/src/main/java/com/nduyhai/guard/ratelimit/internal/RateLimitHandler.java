package com.nduyhai.guard.ratelimit.internal;

import com.nduyhai.guard.annotation.RateLimit;
import com.nduyhai.guard.aop.GuardHandler;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.aop.GuardOperationInvoker;
import com.nduyhai.guard.core.ratelimit.GuardRateLimiter;
import com.nduyhai.guard.core.ratelimit.RateLimitExceededException;
import com.nduyhai.guard.core.ratelimit.RateLimitOperation;
import com.nduyhai.guard.core.ratelimit.RateLimitResult;
import com.nduyhai.guard.metrics.GuardMetrics;
import com.nduyhai.guard.metrics.GuardMetricsTags;
import com.nduyhai.guard.ratelimit.api.RateLimitKeyResolver;
import com.nduyhai.guard.support.GuardUtils;
import org.jspecify.annotations.Nullable;

/**
 * {@link GuardHandler} that enforces the {@code @RateLimit} contract.
 *
 * <p>Throws {@link RateLimitExceededException} when the caller exceeds the configured limit.
 * Records the following metrics via {@link GuardMetrics}:
 *
 * <ul>
 *   <li>{@code guard.ratelimit.allowed} — request passed
 *   <li>{@code guard.ratelimit.rejected} — limit exceeded
 *   <li>{@code guard.ratelimit.error} — unexpected error during rate-limit check
 * </ul>
 */
public final class RateLimitHandler implements GuardHandler {

  public static final int ORDER = 100;

  private final GuardRateLimiter rateLimiter;
  private final RateLimitKeyResolver keyResolver;
  private final GuardMetrics metrics;

  public RateLimitHandler(
      GuardRateLimiter rateLimiter, RateLimitKeyResolver keyResolver, GuardMetrics metrics) {
    this.rateLimiter = rateLimiter;
    this.keyResolver = keyResolver;
    this.metrics = metrics;
  }

  @Override
  @Nullable
  public Object handle(GuardInvocationContext context, GuardOperationInvoker invoker)
      throws Throwable {
    RateLimit annotation = context.getAnnotation(RateLimit.class);
    if (annotation == null) {
      return invoker.invoke();
    }

    String className = context.getTargetClass().getSimpleName();
    String methodName = context.getMethod().getName();
    String provider = rateLimiter.getClass().getSimpleName();

    String key = keyResolver.resolve(context, annotation);
    RateLimitOperation operation =
        new RateLimitOperation(
            key, annotation.limit(), GuardUtils.parseDuration(annotation.window()));

    RateLimitResult result;
    try {
      result = rateLimiter.tryAcquire(operation);
    } catch (Exception ex) {
      metrics.recordRateLimitError(className, methodName, provider, ex.getClass().getSimpleName());
      throw ex;
    }

    if (!result.allowed()) {
      metrics.recordRateLimitRejected(className, methodName, provider);
      throw new RateLimitExceededException(key, result.retryAfter());
    }

    metrics.recordRateLimitAllowed(className, methodName, provider);
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
