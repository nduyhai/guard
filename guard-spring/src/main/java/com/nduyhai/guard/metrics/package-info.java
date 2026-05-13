/**
 * Guard metrics and observability module.
 *
 * <p>Public API:
 *
 * <ul>
 *   <li>{@link com.nduyhai.guard.metrics.GuardMetrics} — central metrics abstraction
 *   <li>{@link com.nduyhai.guard.metrics.GuardMetricsTags} — low-cardinality tag name constants
 *   <li>{@link com.nduyhai.guard.metrics.GuardObservationConvention} — SPI for custom observation
 *       naming and tagging
 *   <li>{@link com.nduyhai.guard.metrics.GuardObservationDocumentation} — documented observation
 *       definitions
 * </ul>
 *
 * <p>Internal implementations ({@code MicrometerGuardMetrics}, {@code NoopGuardMetrics}, {@code
 * GuardObservationHandler}) are wired by {@code GuardMetricsAutoConfiguration} and must not be
 * referenced directly by application code.
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"aop", "annotation", "support"})
package com.nduyhai.guard.metrics;
