package com.nduyhai.guard.core.audit;

/**
 * Resolved metadata for an audit-log guard operation.
 *
 * @param action      logical action name (e.g. {@code "CREATE_PAYMENT"})
 * @param description human-readable description for the audit record
 */
public record AuditOperation(String action, String description) {

    public AuditOperation {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Audit action must not be blank");
        }
        if (description == null) {
            description = "";
        }
    }
}
