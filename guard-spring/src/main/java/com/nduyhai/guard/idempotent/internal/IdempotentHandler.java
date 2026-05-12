package com.nduyhai.guard.idempotent.internal;

import com.nduyhai.guard.annotation.Idempotent;
import com.nduyhai.guard.aop.GuardHandler;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.aop.GuardOperationInvoker;
import com.nduyhai.guard.core.idempotent.IdempotentStore;
import com.nduyhai.guard.idempotent.api.IdempotentKeyResolver;
import com.nduyhai.guard.support.GuardUtils;
import java.time.Duration;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/**
 * {@link GuardHandler} that implements the {@code @Idempotent} contract.
 *
 * <p>On the first successful call the result is stored in the {@link IdempotentStore}.
 * Subsequent calls with the same key within the TTL window return the cached result
 * without executing the method again.
 *
 * <p>Exceptions thrown by the method are NOT cached.
 */
public final class IdempotentHandler implements GuardHandler {

    public static final int ORDER = 200;

    private final IdempotentStore store;
    private final IdempotentKeyResolver keyResolver;

    public IdempotentHandler(IdempotentStore store, IdempotentKeyResolver keyResolver) {
        this.store = store;
        this.keyResolver = keyResolver;
    }

    @Override
    @Nullable
    public Object handle(GuardInvocationContext context, GuardOperationInvoker invoker) throws Throwable {
        Idempotent annotation = context.getAnnotation(Idempotent.class);
        if (annotation == null) {
            return invoker.invoke();
        }

        String key = keyResolver.resolve(context, annotation);
        Optional<Object> cached = store.get(key);
        if (cached.isPresent()) {
            return cached.get();
        }

        Object result = invoker.invoke();
        Duration ttl = GuardUtils.parseDuration(annotation.ttl());
        store.put(key, result, ttl);
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
