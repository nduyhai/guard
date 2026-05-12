package com.nduyhai.guard.lock.internal;

import com.nduyhai.guard.annotation.DistributedLock;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.lock.api.LockKeyResolver;
import com.nduyhai.guard.support.SpelExpressionEvaluator;

/** Default {@link LockKeyResolver} that evaluates {@link DistributedLock#key()} as SpEL. */
public final class DefaultLockKeyResolver implements LockKeyResolver {

  private final SpelExpressionEvaluator evaluator;

  public DefaultLockKeyResolver(SpelExpressionEvaluator evaluator) {
    this.evaluator = evaluator;
  }

  @Override
  public String resolve(GuardInvocationContext context, DistributedLock annotation) {
    String key = evaluator.evaluate(annotation.key(), context);
    if (key == null || key.isBlank()) {
      throw new IllegalStateException(
          "DistributedLock key resolved to blank for method: " + context.getQualifiedMethodName());
    }
    return key;
  }
}
