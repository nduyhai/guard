package com.nduyhai.guard.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MicrometerGuardMetricsTests {

  private MeterRegistry registry;
  private MicrometerGuardMetrics metrics;

  @BeforeEach
  void setUp() {
    registry = new SimpleMeterRegistry();
    metrics = new MicrometerGuardMetrics(registry);
  }

  // ---- Rate limit ----

  @Test
  void recordRateLimitAllowed_incrementsCounter() {
    metrics.recordRateLimitAllowed("PaymentService", "processPayment", "SlidingWindowRateLimiter");

    Counter counter = registry.find(MicrometerGuardMetrics.RATELIMIT_ALLOWED).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(1.0);
    assertThat(counter.getId().getTag(GuardMetricsTags.GUARD_TYPE))
        .isEqualTo(GuardMetricsTags.TYPE_RATE_LIMIT);
    assertThat(counter.getId().getTag(GuardMetricsTags.CLASS_NAME)).isEqualTo("PaymentService");
    assertThat(counter.getId().getTag(GuardMetricsTags.METHOD_NAME)).isEqualTo("processPayment");
  }

  @Test
  void recordRateLimitRejected_incrementsCounter() {
    metrics.recordRateLimitRejected("PaymentService", "processPayment", "SlidingWindowRateLimiter");

    Counter counter = registry.find(MicrometerGuardMetrics.RATELIMIT_REJECTED).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(1.0);
  }

  @Test
  void recordRateLimitError_incrementsCounterWithExceptionTag() {
    metrics.recordRateLimitError(
        "PaymentService", "processPayment", "SlidingWindowRateLimiter", "RuntimeException");

    Counter counter = registry.find(MicrometerGuardMetrics.RATELIMIT_ERROR).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(1.0);
    assertThat(counter.getId().getTag(GuardMetricsTags.EXCEPTION)).isEqualTo("RuntimeException");
  }

  // ---- Idempotent ----

  @Test
  void recordIdempotentHit_incrementsCounter() {
    metrics.recordIdempotentHit("OrderService", "createOrder", "InMemoryIdempotentStore");

    Counter counter = registry.find(MicrometerGuardMetrics.IDEMPOTENT_HIT).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(1.0);
    assertThat(counter.getId().getTag(GuardMetricsTags.GUARD_TYPE))
        .isEqualTo(GuardMetricsTags.TYPE_IDEMPOTENT);
  }

  @Test
  void recordIdempotentMiss_incrementsCounter() {
    metrics.recordIdempotentMiss("OrderService", "createOrder", "InMemoryIdempotentStore");

    Counter counter = registry.find(MicrometerGuardMetrics.IDEMPOTENT_MISS).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(1.0);
  }

  @Test
  void recordIdempotentError_includesExceptionTag() {
    metrics.recordIdempotentError(
        "OrderService", "createOrder", "InMemoryIdempotentStore", "IOException");

    Counter counter = registry.find(MicrometerGuardMetrics.IDEMPOTENT_ERROR).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.getId().getTag(GuardMetricsTags.EXCEPTION)).isEqualTo("IOException");
  }

  // ---- Lock ----

  @Test
  void recordLockAcquired_incrementsCounter() {
    metrics.recordLockAcquired("InventoryService", "updateStock", "InMemoryLockProvider");

    Counter counter = registry.find(MicrometerGuardMetrics.LOCK_ACQUIRED).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(1.0);
    assertThat(counter.getId().getTag(GuardMetricsTags.GUARD_TYPE))
        .isEqualTo(GuardMetricsTags.TYPE_LOCK);
  }

  @Test
  void recordLockFailed_incrementsCounter() {
    metrics.recordLockFailed("InventoryService", "updateStock", "InMemoryLockProvider");

    Counter counter = registry.find(MicrometerGuardMetrics.LOCK_FAILED).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(1.0);
  }

  @Test
  void recordLockTimeout_incrementsCounter() {
    metrics.recordLockTimeout("InventoryService", "updateStock", "InMemoryLockProvider");

    Counter counter = registry.find(MicrometerGuardMetrics.LOCK_TIMEOUT).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(1.0);
  }

  @Test
  void recordLockReleased_incrementsCounter() {
    metrics.recordLockReleased("InventoryService", "updateStock", "InMemoryLockProvider");

    Counter counter = registry.find(MicrometerGuardMetrics.LOCK_RELEASED).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(1.0);
  }

  // ---- Audit ----

  @Test
  void recordAuditSuccess_usesActionAsOutcome() {
    metrics.recordAuditSuccess("PaymentService", "processPayment", "PROCESS_PAYMENT");

    Counter counter = registry.find(MicrometerGuardMetrics.AUDIT_SUCCESS).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(1.0);
    assertThat(counter.getId().getTag(GuardMetricsTags.OUTCOME)).isEqualTo("PROCESS_PAYMENT");
  }

  @Test
  void recordAuditFailure_includesExceptionAndAction() {
    metrics.recordAuditFailure(
        "PaymentService", "processPayment", "PROCESS_PAYMENT", "PaymentException");

    Counter counter = registry.find(MicrometerGuardMetrics.AUDIT_FAILURE).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(1.0);
    assertThat(counter.getId().getTag(GuardMetricsTags.EXCEPTION)).isEqualTo("PaymentException");
    assertThat(counter.getId().getTag(GuardMetricsTags.OUTCOME)).isEqualTo("PROCESS_PAYMENT");
  }

  // ---- Execution timer ----

  @Test
  void startTimer_recordsTimerOnSuccess() {
    GuardMetrics.Sample sample =
        metrics.startTimer(
            GuardMetricsTags.TYPE_RATE_LIMIT, "PaymentService", "processPayment", "guard");

    sample.stop(true, GuardMetricsTags.NONE);

    Timer timer = registry.find(MicrometerGuardMetrics.EXECUTION).timer();
    assertThat(timer).isNotNull();
    assertThat(timer.count()).isEqualTo(1);
    assertThat(timer.getId().getTag(GuardMetricsTags.RESULT))
        .isEqualTo(GuardMetricsTags.RESULT_SUCCESS);
    assertThat(timer.getId().getTag(GuardMetricsTags.EXCEPTION)).isEqualTo(GuardMetricsTags.NONE);
  }

  @Test
  void startTimer_recordsTimerOnFailure() {
    GuardMetrics.Sample sample =
        metrics.startTimer(
            GuardMetricsTags.TYPE_LOCK, "InventoryService", "updateStock", "InMemoryLockProvider");

    sample.stop(false, "LockException");

    Timer timer = registry.find(MicrometerGuardMetrics.EXECUTION).timer();
    assertThat(timer).isNotNull();
    assertThat(timer.count()).isEqualTo(1);
    assertThat(timer.getId().getTag(GuardMetricsTags.RESULT))
        .isEqualTo(GuardMetricsTags.RESULT_FAILURE);
    assertThat(timer.getId().getTag(GuardMetricsTags.EXCEPTION)).isEqualTo("LockException");
  }

  // ---- Noop ----

  @Test
  void noopGuardMetrics_doesNotThrow() {
    NoopGuardMetrics noop = new NoopGuardMetrics();
    noop.recordRateLimitAllowed("A", "b", "C");
    noop.recordRateLimitRejected("A", "b", "C");
    noop.recordRateLimitError("A", "b", "C", "E");
    noop.recordIdempotentHit("A", "b", "C");
    noop.recordIdempotentMiss("A", "b", "C");
    noop.recordIdempotentError("A", "b", "C", "E");
    noop.recordLockAcquired("A", "b", "C");
    noop.recordLockFailed("A", "b", "C");
    noop.recordLockTimeout("A", "b", "C");
    noop.recordLockReleased("A", "b", "C");
    noop.recordAuditSuccess("A", "b", "action");
    noop.recordAuditFailure("A", "b", "action", "E");

    GuardMetrics.Sample sample = noop.startTimer("type", "A", "b", "C");
    sample.stop(true, GuardMetricsTags.NONE);
  }

  // ---- Accumulation ----

  @Test
  void multipleRecordsAccumulate() {
    metrics.recordRateLimitAllowed("Svc", "method", "provider");
    metrics.recordRateLimitAllowed("Svc", "method", "provider");
    metrics.recordRateLimitAllowed("Svc", "method", "provider");

    Counter counter = registry.find(MicrometerGuardMetrics.RATELIMIT_ALLOWED).counter();
    assertThat(counter).isNotNull();
    assertThat(counter.count()).isEqualTo(3.0);
  }
}
