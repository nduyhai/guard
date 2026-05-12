package com.nduyhai.guard.core.audit;

import java.time.Instant;

/**
 * Immutable snapshot of a single guarded-method invocation for auditing.
 *
 * @param action        the logical action name from {@link AuditOperation}
 * @param qualifiedMethod fully-qualified {@code ClassName#methodName} identifier
 * @param args          method arguments at invocation time (may be {@code null} entries)
 * @param result        return value; {@code null} for void methods or on exception
 * @param exception     thrown exception; {@code null} on success
 * @param timestamp     when the method was invoked
 * @param durationMs    wall-clock execution time in milliseconds
 */
public record AuditEvent(
        String action,
        String qualifiedMethod,
        Object[] args,
        Object result,
        Throwable exception,
        Instant timestamp,
        long durationMs) {

    public boolean succeeded() {
        return exception == null;
    }
}
