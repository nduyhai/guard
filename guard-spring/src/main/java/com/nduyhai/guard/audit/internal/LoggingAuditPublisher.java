package com.nduyhai.guard.audit.internal;

import com.nduyhai.guard.core.audit.AuditEvent;
import com.nduyhai.guard.core.audit.AuditPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AuditPublisher} that writes structured audit records via SLF4J at INFO level.
 *
 * <p>Replace this bean with a custom implementation to forward events to Kafka, a relational audit
 * table, or an observability platform.
 */
public final class LoggingAuditPublisher implements AuditPublisher {

  private static final Logger log = LoggerFactory.getLogger(LoggingAuditPublisher.class);

  @Override
  public void publish(AuditEvent event) {
    try {
      if (event.succeeded()) {
        log.info(
            "[AUDIT] action={} method={} durationMs={} status=SUCCESS",
            event.action(),
            event.qualifiedMethod(),
            event.durationMs());
      } else {
        log.warn(
            "[AUDIT] action={} method={} durationMs={} status=FAILURE error={}",
            event.action(),
            event.qualifiedMethod(),
            event.durationMs(),
            event.exception().getMessage());
      }
    } catch (Exception ignored) {
      // must not throw
    }
  }
}
