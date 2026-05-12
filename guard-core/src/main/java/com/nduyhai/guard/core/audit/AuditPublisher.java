package com.nduyhai.guard.core.audit;

/**
 * SPI for publishing audit events produced by the {@code @AuditLog} handler.
 *
 * <p>Provided built-ins:
 * <ul>
 *   <li>{@code LoggingAuditPublisher} — writes structured log entries via SLF4J</li>
 * </ul>
 *
 * <p>Custom implementations can forward events to Kafka, a relational store, or
 * an observability platform.
 */
public interface AuditPublisher {

    /**
     * Publishes {@code event}. Must not throw; exceptions should be caught and logged internally.
     */
    void publish(AuditEvent event);
}
