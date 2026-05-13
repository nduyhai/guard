package com.nduyhai.guard.metrics;

import io.micrometer.observation.Observation;

/**
 * Micrometer {@link Observation.Context} that carries Guard-specific state through an observation
 * lifecycle.
 *
 * <p>An instance is created per guard-annotated method invocation and populated by {@link
 * GuardObservationHandler} before the observation starts. Low-cardinality values are read by
 * {@link DefaultGuardObservationConvention} to produce metric tags and span attributes.
 */
public final class GuardObservationContext extends Observation.Context {

  private String guardType = GuardMetricsTags.TYPE_MULTI;
  private String className = GuardMetricsTags.NONE;
  private String methodName = GuardMetricsTags.NONE;
  private String provider = GuardMetricsTags.NONE;
  private String result = GuardMetricsTags.RESULT_SUCCESS;

  public String getGuardType() {
    return guardType;
  }

  public void setGuardType(String guardType) {
    this.guardType = guardType;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }
}
