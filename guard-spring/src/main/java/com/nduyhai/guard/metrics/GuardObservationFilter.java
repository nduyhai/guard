package com.nduyhai.guard.metrics;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationFilter;

/**
 * {@link ObservationFilter} that applies Guard-specific post-processing to observations before they
 * are recorded.
 *
 * <p>The current implementation is a pass-through that can be extended to:
 *
 * <ul>
 *   <li>Sanitise exception messages to avoid high-cardinality bleed
 *   <li>Enrich contexts with environment tags (region, cluster)
 *   <li>Drop observations below a configurable latency threshold
 * </ul>
 *
 * <p>Register as a Spring bean — {@code ObservationRegistry} picks it up automatically.
 */
public final class GuardObservationFilter implements ObservationFilter {

  @Override
  public Observation.Context map(Observation.Context context) {
    if (!(context instanceof GuardObservationContext guardContext)) {
      return context;
    }

    // Normalise null result to the failure constant so tag values are always set.
    if (guardContext.getResult() == null) {
      guardContext.setResult(GuardMetricsTags.RESULT_FAILURE);
    }

    return guardContext;
  }
}
