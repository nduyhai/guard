package com.nduyhai.guard.audit.internal;

import com.nduyhai.guard.annotation.AuditLog;
import com.nduyhai.guard.aop.GuardHandler;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.aop.GuardOperationInvoker;
import com.nduyhai.guard.core.audit.AuditEvent;
import com.nduyhai.guard.core.audit.AuditOperation;
import com.nduyhai.guard.core.audit.AuditPublisher;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * {@link GuardHandler} that records an {@link AuditEvent} for every guarded invocation.
 *
 * <p>Executes as the outermost handler (order = 50) so it wraps the entire chain and
 * can capture the final result or exception regardless of what other handlers do.
 */
public final class AuditLogHandler implements GuardHandler {

    public static final int ORDER = 50;

    private final AuditPublisher publisher;

    public AuditLogHandler(AuditPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    @Nullable
    public Object handle(GuardInvocationContext context, GuardOperationInvoker invoker) throws Throwable {
        AuditLog annotation = context.getAnnotation(AuditLog.class);
        if (annotation == null) {
            return invoker.invoke();
        }

        AuditOperation operation = new AuditOperation(annotation.action(), annotation.description());
        Instant start = Instant.now();
        Object result = null;
        Throwable error = null;

        try {
            result = invoker.invoke();
            return result;
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            long durationMs = Instant.now().toEpochMilli() - start.toEpochMilli();
            AuditEvent event = new AuditEvent(
                    operation.action(),
                    context.getQualifiedMethodName(),
                    context.getArgs(),
                    result,
                    error,
                    start,
                    durationMs);
            publisher.publish(event);
        }
    }

    @Override
    public boolean supports(GuardInvocationContext context) {
        return context.hasAnnotation(AuditLog.class);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
