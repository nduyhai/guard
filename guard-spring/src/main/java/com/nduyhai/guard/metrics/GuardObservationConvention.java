package com.nduyhai.guard.metrics;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;

/**
 * SPI for customising the observation name and tags produced by {@link GuardObservationHandler}.
 *
 * <p>Register a bean of this type to override the defaults provided by {@link
 * DefaultGuardObservationConvention}. Only one custom convention is consulted per handler instance.
 *
 * <p>Implementations do not need to override {@link #supportsContext} — the default already
 * restricts to {@link GuardObservationContext}.
 */
public interface GuardObservationConvention
    extends ObservationConvention<GuardObservationContext> {

  @Override
  default boolean supportsContext(Observation.Context context) {
    return context instanceof GuardObservationContext;
  }
}
