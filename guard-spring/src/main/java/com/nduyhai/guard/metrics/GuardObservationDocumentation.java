package com.nduyhai.guard.metrics;

import io.micrometer.common.docs.KeyName;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.docs.ObservationDocumentation;

/**
 * Documents the observations emitted by the Guard framework.
 *
 * <p>Each entry in this enum describes a distinct observation name, its default convention class,
 * and the low-/high-cardinality key names it produces. This documentation drives validation tooling
 * and keeps the instrumentation contract explicit.
 *
 * <p>Usage in tests and tooling:
 *
 * <pre>{@code
 * Observation observation = GuardObservationDocumentation.GUARD_EXECUTION.observation(
 *     customConvention, defaultConvention, () -> ctx, observationRegistry);
 * }</pre>
 */
public enum GuardObservationDocumentation implements ObservationDocumentation {

  /**
   * Wraps the entire guard chain execution for a single method invocation.
   *
   * <p>Creates a {@code guard.execution} timer and, when a compatible tracer is configured, a
   * distributed span.
   */
  GUARD_EXECUTION {
    @Override
    public String getName() {
      return "guard.execution";
    }

    @Override
    public Class<? extends ObservationConvention<? extends Observation.Context>>
        getDefaultConvention() {
      return DefaultGuardObservationConvention.class;
    }

    @Override
    public KeyName[] getLowCardinalityKeyNames() {
      return LowCardinalityKeys.values();
    }

    @Override
    public KeyName[] getHighCardinalityKeyNames() {
      return HighCardinalityKeys.values();
    }
  },

  /** Observation for rate-limit guard operations. */
  GUARD_RATE_LIMIT {
    @Override
    public String getName() {
      return "guard.ratelimit";
    }

    @Override
    public KeyName[] getLowCardinalityKeyNames() {
      return LowCardinalityKeys.values();
    }
  },

  /** Observation for idempotent guard operations. */
  GUARD_IDEMPOTENT {
    @Override
    public String getName() {
      return "guard.idempotent";
    }

    @Override
    public KeyName[] getLowCardinalityKeyNames() {
      return LowCardinalityKeys.values();
    }
  },

  /** Observation for distributed lock guard operations. */
  GUARD_LOCK {
    @Override
    public String getName() {
      return "guard.lock";
    }

    @Override
    public KeyName[] getLowCardinalityKeyNames() {
      return LowCardinalityKeys.values();
    }
  };

  /** Low-cardinality keys safe to use as Prometheus labels. */
  public enum LowCardinalityKeys implements KeyName {
    GUARD_TYPE {
      @Override
      public String asString() {
        return GuardMetricsTags.GUARD_TYPE;
      }
    },
    CLASS {
      @Override
      public String asString() {
        return GuardMetricsTags.CLASS_NAME;
      }
    },
    METHOD {
      @Override
      public String asString() {
        return GuardMetricsTags.METHOD_NAME;
      }
    },
    RESULT {
      @Override
      public String asString() {
        return GuardMetricsTags.RESULT;
      }
    },
    PROVIDER {
      @Override
      public String asString() {
        return GuardMetricsTags.PROVIDER;
      }
    }
  }

  /** High-cardinality keys suitable only for distributed tracing spans. */
  public enum HighCardinalityKeys implements KeyName {
    EXCEPTION {
      @Override
      public String asString() {
        return GuardMetricsTags.EXCEPTION;
      }
    }
  }
}
