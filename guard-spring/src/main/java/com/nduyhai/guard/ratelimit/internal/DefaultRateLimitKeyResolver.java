package com.nduyhai.guard.ratelimit.internal;

import com.nduyhai.guard.annotation.RateLimit;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.ratelimit.api.RateLimitKeyResolver;
import com.nduyhai.guard.support.SpelExpressionEvaluator;

/** Default {@link RateLimitKeyResolver} that evaluates {@link RateLimit#key()} as SpEL. */
public final class DefaultRateLimitKeyResolver implements RateLimitKeyResolver {

  private final SpelExpressionEvaluator evaluator;

  public DefaultRateLimitKeyResolver(SpelExpressionEvaluator evaluator) {
    this.evaluator = evaluator;
  }

  @Override
  public String resolve(GuardInvocationContext context, RateLimit annotation) {
    String key = evaluator.evaluate(annotation.key(), context);
    if (key == null || key.isBlank()) {
      throw new IllegalStateException(
          "RateLimit key resolved to blank for method: " + context.getQualifiedMethodName());
    }
    return key;
  }
}
