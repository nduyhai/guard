package com.nduyhai.guard.metrics;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;

/**
 * Default {@link GuardObservationConvention} that produces Prometheus-friendly metric names and
 * low-cardinality tags following Micrometer naming conventions.
 *
 * <p>The observation timer is registered under {@value #OBSERVATION_NAME}.
 *
 * <p>Low-cardinality tags (safe to use as Prometheus labels):
 *
 * <ul>
 *   <li>{@code guard.type} – rate-limit | idempotent | lock | audit | multi
 *   <li>{@code class} – simple name of the target bean class
 *   <li>{@code method} – method name
 *   <li>{@code result} – success | failure
 *   <li>{@code provider} – simple name of the active SPI implementation
 * </ul>
 *
 * <p>High-cardinality tags (safe only in distributed tracing spans):
 *
 * <ul>
 *   <li>{@code exception} – simple name of any thrown exception, or {@code none}
 * </ul>
 */
public class DefaultGuardObservationConvention implements GuardObservationConvention {

  public static final String OBSERVATION_NAME = "guard.execution";

  @Override
  public String getName() {
    return OBSERVATION_NAME;
  }

  @Override
  public String getContextualName(GuardObservationContext context) {
    return "guard " + context.getGuardType() + " " + context.getMethodName();
  }

  @Override
  public KeyValues getLowCardinalityKeyValues(GuardObservationContext context) {
    return KeyValues.of(
        KeyValue.of(GuardMetricsTags.GUARD_TYPE, context.getGuardType()),
        KeyValue.of(GuardMetricsTags.CLASS_NAME, context.getClassName()),
        KeyValue.of(GuardMetricsTags.METHOD_NAME, context.getMethodName()),
        KeyValue.of(GuardMetricsTags.RESULT, context.getResult()),
        KeyValue.of(GuardMetricsTags.PROVIDER, context.getProvider()));
  }

  @Override
  public KeyValues getHighCardinalityKeyValues(GuardObservationContext context) {
    Throwable error = context.getError();
    String exceptionType = error != null ? error.getClass().getSimpleName() : GuardMetricsTags.NONE;
    return KeyValues.of(KeyValue.of(GuardMetricsTags.EXCEPTION, exceptionType));
  }
}
