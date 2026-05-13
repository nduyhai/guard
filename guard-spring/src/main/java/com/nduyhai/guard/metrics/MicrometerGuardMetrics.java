package com.nduyhai.guard.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

/**
 * {@link GuardMetrics} implementation backed by Micrometer.
 *
 * <p>Registers the following meters:
 *
 * <ul>
 *   <li>{@code guard.ratelimit.allowed} – counter, tags: guard.type, class, method, provider
 *   <li>{@code guard.ratelimit.rejected} – counter, tags: guard.type, class, method, provider
 *   <li>{@code guard.ratelimit.error} – counter, tags: guard.type, class, method, provider,
 *       exception
 *   <li>{@code guard.idempotent.hit} – counter, tags: guard.type, class, method, provider
 *   <li>{@code guard.idempotent.miss} – counter, tags: guard.type, class, method, provider
 *   <li>{@code guard.idempotent.error} – counter, tags: guard.type, class, method, provider,
 *       exception
 *   <li>{@code guard.lock.acquired} – counter, tags: guard.type, class, method, provider
 *   <li>{@code guard.lock.failed} – counter, tags: guard.type, class, method, provider
 *   <li>{@code guard.lock.timeout} – counter, tags: guard.type, class, method, provider
 *   <li>{@code guard.lock.released} – counter, tags: guard.type, class, method, provider
 *   <li>{@code guard.audit.success} – counter, tags: guard.type, class, method, outcome
 *   <li>{@code guard.audit.failure} – counter, tags: guard.type, class, method, outcome, exception
 *   <li>{@code guard.execution} – timer, tags: guard.type, class, method, provider, result,
 *       exception
 * </ul>
 *
 * <p>All tag values are low-cardinality. Dynamic identifiers such as user IDs or request IDs must
 * never be used as tag values.
 */
public final class MicrometerGuardMetrics implements GuardMetrics {

  // ---- Metric names ----

  static final String RATELIMIT_ALLOWED = "guard.ratelimit.allowed";
  static final String RATELIMIT_REJECTED = "guard.ratelimit.rejected";
  static final String RATELIMIT_ERROR = "guard.ratelimit.error";

  static final String IDEMPOTENT_HIT = "guard.idempotent.hit";
  static final String IDEMPOTENT_MISS = "guard.idempotent.miss";
  static final String IDEMPOTENT_ERROR = "guard.idempotent.error";

  static final String LOCK_ACQUIRED = "guard.lock.acquired";
  static final String LOCK_FAILED = "guard.lock.failed";
  static final String LOCK_TIMEOUT = "guard.lock.timeout";
  static final String LOCK_RELEASED = "guard.lock.released";

  static final String AUDIT_SUCCESS = "guard.audit.success";
  static final String AUDIT_FAILURE = "guard.audit.failure";

  static final String EXECUTION = "guard.execution";

  private final MeterRegistry registry;

  public MicrometerGuardMetrics(MeterRegistry registry) {
    this.registry = registry;
  }

  // ---- Rate limit ----

  @Override
  public void recordRateLimitAllowed(String className, String methodName, String provider) {
    counter(
            RATELIMIT_ALLOWED,
            "Rate limit allowed requests",
            baseTags(GuardMetricsTags.TYPE_RATE_LIMIT, className, methodName, provider))
        .increment();
  }

  @Override
  public void recordRateLimitRejected(String className, String methodName, String provider) {
    counter(
            RATELIMIT_REJECTED,
            "Rate limit rejected requests",
            baseTags(GuardMetricsTags.TYPE_RATE_LIMIT, className, methodName, provider))
        .increment();
  }

  @Override
  public void recordRateLimitError(
      String className, String methodName, String provider, String exceptionType) {
    counter(
            RATELIMIT_ERROR,
            "Rate limit processing errors",
            baseTags(GuardMetricsTags.TYPE_RATE_LIMIT, className, methodName, provider)
                .and(Tag.of(GuardMetricsTags.EXCEPTION, exceptionType)))
        .increment();
  }

  // ---- Idempotent ----

  @Override
  public void recordIdempotentHit(String className, String methodName, String provider) {
    counter(
            IDEMPOTENT_HIT,
            "Idempotent cache hits — result replayed from store",
            baseTags(GuardMetricsTags.TYPE_IDEMPOTENT, className, methodName, provider))
        .increment();
  }

  @Override
  public void recordIdempotentMiss(String className, String methodName, String provider) {
    counter(
            IDEMPOTENT_MISS,
            "Idempotent cache misses — method executed and result cached",
            baseTags(GuardMetricsTags.TYPE_IDEMPOTENT, className, methodName, provider))
        .increment();
  }

  @Override
  public void recordIdempotentError(
      String className, String methodName, String provider, String exceptionType) {
    counter(
            IDEMPOTENT_ERROR,
            "Idempotent store or method errors",
            baseTags(GuardMetricsTags.TYPE_IDEMPOTENT, className, methodName, provider)
                .and(Tag.of(GuardMetricsTags.EXCEPTION, exceptionType)))
        .increment();
  }

  // ---- Lock ----

  @Override
  public void recordLockAcquired(String className, String methodName, String provider) {
    counter(
            LOCK_ACQUIRED,
            "Distributed lock acquisitions",
            baseTags(GuardMetricsTags.TYPE_LOCK, className, methodName, provider))
        .increment();
  }

  @Override
  public void recordLockFailed(String className, String methodName, String provider) {
    counter(
            LOCK_FAILED,
            "Distributed lock acquisition failures",
            baseTags(GuardMetricsTags.TYPE_LOCK, className, methodName, provider))
        .increment();
  }

  @Override
  public void recordLockTimeout(String className, String methodName, String provider) {
    counter(
            LOCK_TIMEOUT,
            "Distributed lock acquisition timeouts",
            baseTags(GuardMetricsTags.TYPE_LOCK, className, methodName, provider))
        .increment();
  }

  @Override
  public void recordLockReleased(String className, String methodName, String provider) {
    counter(
            LOCK_RELEASED,
            "Distributed lock releases",
            baseTags(GuardMetricsTags.TYPE_LOCK, className, methodName, provider))
        .increment();
  }

  // ---- Audit ----

  @Override
  public void recordAuditSuccess(String className, String methodName, String action) {
    counter(
            AUDIT_SUCCESS,
            "Successful audit log events",
            Tags.of(
                Tag.of(GuardMetricsTags.GUARD_TYPE, GuardMetricsTags.TYPE_AUDIT),
                Tag.of(GuardMetricsTags.CLASS_NAME, className),
                Tag.of(GuardMetricsTags.METHOD_NAME, methodName),
                Tag.of(GuardMetricsTags.OUTCOME, action)))
        .increment();
  }

  @Override
  public void recordAuditFailure(
      String className, String methodName, String action, String exceptionType) {
    counter(
            AUDIT_FAILURE,
            "Failed audit log events",
            Tags.of(
                Tag.of(GuardMetricsTags.GUARD_TYPE, GuardMetricsTags.TYPE_AUDIT),
                Tag.of(GuardMetricsTags.CLASS_NAME, className),
                Tag.of(GuardMetricsTags.METHOD_NAME, methodName),
                Tag.of(GuardMetricsTags.OUTCOME, action),
                Tag.of(GuardMetricsTags.EXCEPTION, exceptionType)))
        .increment();
  }

  // ---- Execution latency ----

  @Override
  public Sample startTimer(
      String guardType, String className, String methodName, String provider) {
    Timer.Sample timerSample = Timer.start(registry);
    return (success, exceptionType) -> {
      Tags tags =
          Tags.of(
              Tag.of(GuardMetricsTags.GUARD_TYPE, guardType),
              Tag.of(GuardMetricsTags.CLASS_NAME, className),
              Tag.of(GuardMetricsTags.METHOD_NAME, methodName),
              Tag.of(GuardMetricsTags.PROVIDER, provider),
              Tag.of(
                  GuardMetricsTags.RESULT,
                  success ? GuardMetricsTags.RESULT_SUCCESS : GuardMetricsTags.RESULT_FAILURE),
              Tag.of(GuardMetricsTags.EXCEPTION, exceptionType));
      Timer timer =
          Timer.builder(EXECUTION)
              .description("Total guard chain execution latency")
              .tags(tags)
              .register(registry);
      timerSample.stop(timer);
    };
  }

  // ---- Helpers ----

  private Counter counter(String name, String description, Tags tags) {
    return Counter.builder(name).description(description).tags(tags).register(registry);
  }

  private Tags baseTags(String guardType, String className, String methodName, String provider) {
    return Tags.of(
        Tag.of(GuardMetricsTags.GUARD_TYPE, guardType),
        Tag.of(GuardMetricsTags.CLASS_NAME, className),
        Tag.of(GuardMetricsTags.METHOD_NAME, methodName),
        Tag.of(GuardMetricsTags.PROVIDER, provider));
  }
}
