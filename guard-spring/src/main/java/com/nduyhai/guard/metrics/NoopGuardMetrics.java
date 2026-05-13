package com.nduyhai.guard.metrics;

/**
 * Zero-overhead {@link GuardMetrics} implementation used when no metrics infrastructure is
 * available on the classpath.
 *
 * <p>All methods are empty; the {@link Sample} returned by {@link #startTimer} is a singleton that
 * discards all calls.
 */
public final class NoopGuardMetrics implements GuardMetrics {

  private static final Sample NOOP_SAMPLE = (success, exceptionType) -> {};

  @Override
  public void recordRateLimitAllowed(String className, String methodName, String provider) {}

  @Override
  public void recordRateLimitRejected(String className, String methodName, String provider) {}

  @Override
  public void recordRateLimitError(
      String className, String methodName, String provider, String exceptionType) {}

  @Override
  public void recordIdempotentHit(String className, String methodName, String provider) {}

  @Override
  public void recordIdempotentMiss(String className, String methodName, String provider) {}

  @Override
  public void recordIdempotentError(
      String className, String methodName, String provider, String exceptionType) {}

  @Override
  public void recordLockAcquired(String className, String methodName, String provider) {}

  @Override
  public void recordLockFailed(String className, String methodName, String provider) {}

  @Override
  public void recordLockTimeout(String className, String methodName, String provider) {}

  @Override
  public void recordLockReleased(String className, String methodName, String provider) {}

  @Override
  public void recordAuditSuccess(String className, String methodName, String action) {}

  @Override
  public void recordAuditFailure(
      String className, String methodName, String action, String exceptionType) {}

  @Override
  public Sample startTimer(
      String guardType, String className, String methodName, String provider) {
    return NOOP_SAMPLE;
  }
}
