package com.nduyhai.guard.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import com.nduyhai.guard.metrics.DefaultGuardObservationConvention;
import com.nduyhai.guard.metrics.GuardMetrics;
import com.nduyhai.guard.metrics.GuardObservationFilter;
import com.nduyhai.guard.metrics.GuardObservationHandler;
import com.nduyhai.guard.metrics.MicrometerGuardMetrics;
import com.nduyhai.guard.metrics.NoopGuardMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class GuardMetricsAutoConfigurationTests {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(
              AutoConfigurations.of(
                  GuardMetricsAutoConfiguration.class, GuardAutoConfiguration.class));

  @Test
  void registersMicrometerGuardMetricsWhenMicrometerIsPresent() {
    runner
        .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
        .run(
            ctx -> {
              assertThat(ctx).hasSingleBean(GuardMetrics.class);
              assertThat(ctx.getBean(GuardMetrics.class)).isInstanceOf(MicrometerGuardMetrics.class);
            });
  }

  @Test
  void registersNoopGuardMetricsWhenMicrometerIsAbsent() {
    runner
        .withClassLoader(new FilteredClassLoader(MeterRegistry.class))
        .run(
            ctx -> {
              assertThat(ctx).hasSingleBean(GuardMetrics.class);
              assertThat(ctx.getBean(GuardMetrics.class)).isInstanceOf(NoopGuardMetrics.class);
            });
  }

  @Test
  void registersObservationHandlerWhenObservationRegistryIsPresent() {
    runner
        .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
        .withBean(ObservationRegistry.class, ObservationRegistry::create)
        .run(ctx -> assertThat(ctx).hasSingleBean(GuardObservationHandler.class));
  }

  @Test
  void doesNotRegisterObservationHandlerWhenObservationRegistryIsAbsent() {
    runner
        .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
        .run(ctx -> assertThat(ctx).doesNotHaveBean(GuardObservationHandler.class));
  }

  @Test
  void registersDefaultObservationConvention() {
    runner
        .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
        .run(ctx -> assertThat(ctx).hasSingleBean(DefaultGuardObservationConvention.class));
  }

  @Test
  void registersGuardObservationFilter() {
    runner
        .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
        .run(ctx -> assertThat(ctx).hasSingleBean(GuardObservationFilter.class));
  }

  @Test
  void customGuardMetricsBeanTakesPrecedence() {
    runner
        .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
        .withBean("customMetrics", GuardMetrics.class, NoopGuardMetrics::new)
        .run(
            ctx -> {
              assertThat(ctx).hasSingleBean(GuardMetrics.class);
              assertThat(ctx.getBean(GuardMetrics.class)).isInstanceOf(NoopGuardMetrics.class);
            });
  }

  @Test
  void disabledWhenGuardPropertyIsFalse() {
    runner
        .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
        .withPropertyValues("guard.enabled=false")
        .run(ctx -> assertThat(ctx).doesNotHaveBean(GuardMetrics.class));
  }
}
