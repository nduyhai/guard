package com.nduyhai.guard.annotation;

import java.lang.annotation.*;

/**
 * Records an audit event for every invocation of the annotated method, capturing the action name,
 * arguments, result, exception (if any), and wall-clock duration.
 *
 * <pre>{@code
 * @AuditLog(action = "CREATE_PAYMENT")
 * public PaymentResponse createPayment(PaymentRequest request) { ... }
 * }</pre>
 *
 * <p>Audit events are published via {@link com.nduyhai.guard.core.audit.AuditPublisher}. The
 * default implementation logs via SLF4J; replace the bean to forward events to Kafka, a relational
 * store, or an observability platform.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

  /** Logical action identifier written to the audit record. */
  String action();

  /** Optional human-readable description. */
  String description() default "";
}
