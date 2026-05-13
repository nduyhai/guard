package com.nduyhai.guard.metrics;

/**
 * Central metrics abstraction for the Guard framework.
 *
 * <p>Decouples guard handlers from any specific metrics library. The default implementation
 * delegates to Micrometer when available; a {@link NoopGuardMetrics} is used as a zero-overhead
 * fallback when no metrics infrastructure is present.
 *
 * <p>All tag values passed to these methods must be low-cardinality (no user IDs, request IDs, or
 * dynamic payload data). Use {@link GuardMetricsTags} constants for tag names and common values.
 */
public interface GuardMetrics {

  // ---- Rate limit ----

  void recordRateLimitAllowed(String className, String methodName, String provider);

  void recordRateLimitRejected(String className, String methodName, String provider);

  void recordRateLimitError(
      String className, String methodName, String provider, String exceptionType);

  // ---- Idempotent ----

  void recordIdempotentHit(String className, String methodName, String provider);

  void recordIdempotentMiss(String className, String methodName, String provider);

  void recordIdempotentError(
      String className, String methodName, String provider, String exceptionType);

  // ---- Distributed lock ----

  void recordLockAcquired(String className, String methodName, String provider);

  void recordLockFailed(String className, String methodName, String provider);

  void recordLockTimeout(String className, String methodName, String provider);

  void recordLockReleased(String className, String methodName, String provider);

  // ---- Audit ----

  void recordAuditSuccess(String className, String methodName, String action);

  void recordAuditFailure(
      String className, String methodName, String action, String exceptionType);

  // ---- Execution latency ----

  /**
   * Starts a latency sample for a guard execution. Callers MUST call {@link Sample#stop} in a
   * {@code finally} block to ensure the timer is always recorded.
   *
   * @param guardType the guard type (use {@link GuardMetricsTags} constants)
   * @param className the simple name of the target class
   * @param methodName the name of the intercepted method
   * @param provider the simple class name of the active SPI implementation
   * @return an opaque sample handle
   */
  Sample startTimer(String guardType, String className, String methodName, String provider);

  /**
   * An opaque handle returned by {@link #startTimer} that records the elapsed time when stopped.
   *
   * <p>Implementations must be safe to call exactly once. The noop variant silently discards all
   * calls.
   */
  interface Sample {

    /**
     * Stops the timer and records the measurement.
     *
     * @param success {@code true} if the guarded invocation completed without error
     * @param exceptionType simple class name of the thrown exception, or {@link
     *     GuardMetricsTags#NONE} when {@code success} is {@code true}
     */
    void stop(boolean success, String exceptionType);
  }
}
