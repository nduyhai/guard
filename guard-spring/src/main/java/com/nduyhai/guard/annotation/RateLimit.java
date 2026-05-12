package com.nduyhai.guard.annotation;

import java.lang.annotation.*;

/**
 * Limits the number of invocations of the annotated method within a sliding time window. Exceeding
 * the limit raises a {@link com.nduyhai.guard.core.ratelimit.RateLimitExceededException}.
 *
 * <pre>{@code
 * @RateLimit(key = "#merchantId", limit = 100, window = "1m")
 * public void processPayment(String merchantId, ...) { ... }
 * }</pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

  /** SpEL expression that resolves to the rate-limit bucket key. */
  String key();

  /** Maximum number of calls allowed within {@link #window()}. */
  long limit() default 100;

  /**
   * Sliding time window (e.g. {@code "1m"}, {@code "30s"}, {@code "PT1H"}). Defaults to {@code
   * "1m"}.
   */
  String window() default "1m";
}
