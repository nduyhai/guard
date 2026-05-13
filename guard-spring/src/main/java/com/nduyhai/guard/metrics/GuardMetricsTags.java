package com.nduyhai.guard.metrics;

/**
 * Canonical tag name constants and low-cardinality value constants for all Guard metrics.
 *
 * <p>Use these constants when building {@code Tags} or {@code KeyValues} to ensure consistency
 * across Micrometer counters, timers, and Observation conventions.
 */
public final class GuardMetricsTags {

  // ---- Tag names ----

  public static final String GUARD_TYPE = "guard.type";
  public static final String CLASS_NAME = "class";
  public static final String METHOD_NAME = "method";
  public static final String RESULT = "result";
  public static final String PROVIDER = "provider";
  public static final String EXCEPTION = "exception";
  public static final String OUTCOME = "outcome";

  // ---- Guard type values (guard.type tag) ----

  public static final String TYPE_RATE_LIMIT = "rate-limit";
  public static final String TYPE_IDEMPOTENT = "idempotent";
  public static final String TYPE_LOCK = "lock";
  public static final String TYPE_AUDIT = "audit";
  public static final String TYPE_MULTI = "multi";

  // ---- Result values (result tag) ----

  public static final String RESULT_SUCCESS = "success";
  public static final String RESULT_FAILURE = "failure";

  // ---- Outcome values (outcome tag) ----

  public static final String OUTCOME_ALLOWED = "allowed";
  public static final String OUTCOME_REJECTED = "rejected";
  public static final String OUTCOME_ERROR = "error";
  public static final String OUTCOME_HIT = "hit";
  public static final String OUTCOME_MISS = "miss";
  public static final String OUTCOME_ACQUIRED = "acquired";
  public static final String OUTCOME_FAILED = "failed";
  public static final String OUTCOME_TIMEOUT = "timeout";
  public static final String OUTCOME_RELEASED = "released";

  /** Placeholder when no exception is present — keeps tag cardinality stable. */
  public static final String NONE = "none";

  private GuardMetricsTags() {}
}
