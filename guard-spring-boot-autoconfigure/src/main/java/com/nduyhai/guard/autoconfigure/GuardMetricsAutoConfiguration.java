package com.nduyhai.guard.autoconfigure;

import com.nduyhai.guard.metrics.DefaultGuardObservationConvention;
import com.nduyhai.guard.metrics.GuardMetrics;
import com.nduyhai.guard.metrics.GuardObservationConvention;
import com.nduyhai.guard.metrics.GuardObservationFilter;
import com.nduyhai.guard.metrics.GuardObservationHandler;
import com.nduyhai.guard.metrics.MicrometerGuardMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

/**
 * Spring Boot auto-configuration for Guard metrics and observability.
 *
 * <p>Activates only when:
 *
 * <ol>
 *   <li>{@code guard.enabled=true} (the default)
 *   <li>{@link MeterRegistry} is on the classpath (i.e. {@code micrometer-core} is a dependency)
 * </ol>
 *
 * <p>Registers the following beans:
 *
 * <ul>
 *   <li>{@link MicrometerGuardMetrics} — implements {@link GuardMetrics} using Micrometer counters
 *       and timers
 *   <li>{@link DefaultGuardObservationConvention} — default Observation naming and tag convention
 *   <li>{@link GuardObservationFilter} — normalises Guard observation contexts
 *   <li>{@link GuardObservationHandler} — wraps the guard chain in a Micrometer Observation (only
 *       when {@link ObservationRegistry} is also present)
 * </ul>
 *
 * <p>All beans respect {@code @ConditionalOnMissingBean} so any can be replaced by a custom
 * declaration.
 *
 * <p>This configuration runs {@code before = GuardAutoConfiguration.class} so the {@link
 * GuardMetrics} bean is available when handlers are instantiated.
 */
@AutoConfiguration(before = GuardAutoConfiguration.class)
@ConditionalOnProperty(
    prefix = "guard",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@ConditionalOnClass(MeterRegistry.class)
public class GuardMetricsAutoConfiguration {

  // ---- Core metrics ----

  @Bean
  @ConditionalOnMissingBean(GuardMetrics.class)
  public MicrometerGuardMetrics guardMetrics(MeterRegistry meterRegistry) {
    return new MicrometerGuardMetrics(meterRegistry);
  }

  // ---- Observation convention ----

  @Bean
  @ConditionalOnMissingBean(DefaultGuardObservationConvention.class)
  public DefaultGuardObservationConvention defaultGuardObservationConvention() {
    return new DefaultGuardObservationConvention();
  }

  // ---- Observation filter ----

  @Bean
  @ConditionalOnMissingBean(GuardObservationFilter.class)
  public GuardObservationFilter guardObservationFilter() {
    return new GuardObservationFilter();
  }

  // ---- Observation handler (requires ObservationRegistry) ----

  @Bean
  @ConditionalOnMissingBean(GuardObservationHandler.class)
  @ConditionalOnClass(ObservationRegistry.class)
  @ConditionalOnBean(ObservationRegistry.class)
  public GuardObservationHandler guardObservationHandler(
      ObservationRegistry observationRegistry,
      @Autowired(required = false) @Nullable GuardObservationConvention customConvention) {
    return new GuardObservationHandler(observationRegistry, customConvention);
  }
}
