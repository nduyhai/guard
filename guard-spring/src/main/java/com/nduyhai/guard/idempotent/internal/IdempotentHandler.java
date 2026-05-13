package com.nduyhai.guard.idempotent.internal;

import com.nduyhai.guard.annotation.Idempotent;
import com.nduyhai.guard.aop.GuardHandler;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.aop.GuardOperationInvoker;
import com.nduyhai.guard.core.idempotent.IdempotentStore;
import com.nduyhai.guard.idempotent.api.IdempotentKeyResolver;
import com.nduyhai.guard.metrics.GuardMetrics;
import com.nduyhai.guard.support.GuardUtils;
import java.time.Duration;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/**
 * {@link GuardHandler} that implements the {@code @Idempotent} contract.
 *
 * <p>On the first successful call the result is stored in the {@link IdempotentStore}. Subsequent
 * calls with the same key within the TTL window return the cached result without executing the
 * method again.
 *
 * <p>Exceptions thrown by the method are NOT cached.
 *
 * <p>Records the following metrics via {@link GuardMetrics}:
 *
 * <ul>
 *   <li>{@code guard.idempotent.hit} — cached result replayed
 *   <li>{@code guard.idempotent.miss} — method executed and result stored
 *   <li>{@code guard.idempotent.error} — unexpected error during store interaction
 * </ul>
 */
public final class IdempotentHandler implements GuardHandler {

  public static final int ORDER = 200;

  private final IdempotentStore store;
  private final IdempotentKeyResolver keyResolver;
  private final GuardMetrics metrics;

  public IdempotentHandler(
      IdempotentStore store, IdempotentKeyResolver keyResolver, GuardMetrics metrics) {
    this.store = store;
    this.keyResolver = keyResolver;
    this.metrics = metrics;
  }

  @Override
  @Nullable
  public Object handle(GuardInvocationContext context, GuardOperationInvoker invoker)
      throws Throwable {
    Idempotent annotation = context.getAnnotation(Idempotent.class);
    if (annotation == null) {
      return invoker.invoke();
    }

    String className = context.getTargetClass().getSimpleName();
    String methodName = context.getMethod().getName();
    String provider = store.getClass().getSimpleName();

    String key = keyResolver.resolve(context, annotation);

    Optional<Object> cached;
    try {
      cached = store.get(key);
    } catch (Exception ex) {
      metrics.recordIdempotentError(className, methodName, provider, ex.getClass().getSimpleName());
      throw ex;
    }

    if (cached.isPresent()) {
      metrics.recordIdempotentHit(className, methodName, provider);
      return cached.get();
    }

    Object result;
    try {
      result = invoker.invoke();
    } catch (Exception ex) {
      metrics.recordIdempotentError(className, methodName, provider, ex.getClass().getSimpleName());
      throw ex;
    }

    Duration ttl = GuardUtils.parseDuration(annotation.ttl());
    try {
      store.put(key, result, ttl);
    } catch (Exception ex) {
      metrics.recordIdempotentError(className, methodName, provider, ex.getClass().getSimpleName());
      throw ex;
    }

    metrics.recordIdempotentMiss(className, methodName, provider);
    return result;
  }

  @Override
  public boolean supports(GuardInvocationContext context) {
    return context.hasAnnotation(Idempotent.class);
  }

  @Override
  public int getOrder() {
    return ORDER;
  }
}
