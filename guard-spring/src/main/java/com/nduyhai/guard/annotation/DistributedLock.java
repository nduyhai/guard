package com.nduyhai.guard.annotation;

import java.lang.annotation.*;

/**
 * Acquires a distributed lock before executing the annotated method and releases it unconditionally
 * upon return (normal or exceptional).
 *
 * <pre>{@code
 * @DistributedLock(key = "'payment:' + #request.orderId", leaseTime = "30s")
 * public PaymentResponse pay(PaymentRequest request) { ... }
 * }</pre>
 *
 * <p>The {@link #key()} attribute is a SpEL expression evaluated against method parameters.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

  /** SpEL expression that resolves to the lock key. */
  String key();

  /** Maximum time to wait for the lock. Defaults to {@code "5s"}. */
  String waitTime() default "5s";

  /** How long the lock is held before automatic expiry. Defaults to {@code "30s"}. */
  String leaseTime() default "30s";
}
