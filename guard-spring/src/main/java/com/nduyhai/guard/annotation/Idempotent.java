package com.nduyhai.guard.annotation;

import java.lang.annotation.*;

/**
 * Guards a method for idempotency: the first successful result is stored and replayed for
 * subsequent calls that share the same resolved {@link #key()}.
 *
 * <pre>{@code
 * @Idempotent(key = "#request.orderId", ttl = "10m")
 * public OrderResponse createOrder(OrderRequest request) { ... }
 * }</pre>
 *
 * <p>The {@link #key()} attribute is a SpEL expression evaluated against method parameters. The
 * {@link #ttl()} attribute accepts durations in the format {@code Ns} (seconds), {@code Nm}
 * (minutes), {@code Nh} (hours), or ISO-8601 (e.g. {@code PT10M}).
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

  /** SpEL expression that resolves to the idempotency key. */
  String key();

  /** Time-to-live for the cached result (e.g. {@code "10m"}, {@code "PT1H"}). */
  String ttl() default "10m";
}
