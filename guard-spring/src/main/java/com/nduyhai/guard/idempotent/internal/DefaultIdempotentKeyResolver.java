package com.nduyhai.guard.idempotent.internal;

import com.nduyhai.guard.annotation.Idempotent;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.idempotent.api.IdempotentKeyResolver;
import com.nduyhai.guard.support.SpelExpressionEvaluator;

/** Default {@link IdempotentKeyResolver} that evaluates {@link Idempotent#key()} as SpEL. */
public final class DefaultIdempotentKeyResolver implements IdempotentKeyResolver {

  private final SpelExpressionEvaluator evaluator;

  public DefaultIdempotentKeyResolver(SpelExpressionEvaluator evaluator) {
    this.evaluator = evaluator;
  }

  @Override
  public String resolve(GuardInvocationContext context, Idempotent annotation) {
    String key = evaluator.evaluate(annotation.key(), context);
    if (key == null || key.isBlank()) {
      throw new IllegalStateException(
          "Idempotent key resolved to blank for method: " + context.getQualifiedMethodName());
    }
    return key;
  }
}
