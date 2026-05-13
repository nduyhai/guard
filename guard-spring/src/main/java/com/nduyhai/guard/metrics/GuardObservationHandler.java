package com.nduyhai.guard.metrics;

import com.nduyhai.guard.annotation.AuditLog;
import com.nduyhai.guard.annotation.DistributedLock;
import com.nduyhai.guard.annotation.Idempotent;
import com.nduyhai.guard.annotation.RateLimit;
import com.nduyhai.guard.aop.GuardHandler;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.aop.GuardOperationInvoker;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.jspecify.annotations.Nullable;

/**
 * {@link GuardHandler} that wraps every guarded method invocation in a Micrometer {@link
 * Observation}.
 *
 * <p>The observation is registered under the name {@value
 * DefaultGuardObservationConvention#OBSERVATION_NAME} and creates:
 *
 * <ul>
 *   <li>a {@code guard.execution} timer (via the attached {@link
 *       io.micrometer.core.instrument.MeterRegistry})
 *   <li>a distributed span when a compatible tracer (Brave, OpenTelemetry) is on the classpath
 * </ul>
 *
 * <p>This handler is only registered by {@code GuardMetricsAutoConfiguration} when {@link
 * ObservationRegistry} is available. It runs as the outermost handler (order = {@link
 * #ORDER}) so it spans the entire guard chain including all other handlers.
 */
public final class GuardObservationHandler implements GuardHandler {

  /** Executes before all other guard handlers so it encompasses the entire chain. */
  public static final int ORDER = Integer.MIN_VALUE + 100;

  private final ObservationRegistry observationRegistry;
  private final DefaultGuardObservationConvention defaultConvention;

  @Nullable private final GuardObservationConvention customConvention;

  public GuardObservationHandler(
      ObservationRegistry observationRegistry,
      @Nullable GuardObservationConvention customConvention) {
    this.observationRegistry = observationRegistry;
    this.defaultConvention = new DefaultGuardObservationConvention();
    this.customConvention = customConvention;
  }

  @Override
  @Nullable
  public Object handle(GuardInvocationContext context, GuardOperationInvoker invoker)
      throws Throwable {
    GuardObservationContext observationContext = buildObservationContext(context);

    Observation observation =
        GuardObservationDocumentation.GUARD_EXECUTION.observation(
            customConvention, defaultConvention, () -> observationContext, observationRegistry);

    observation.start();
    try {
      Object result = invoker.invoke();
      observationContext.setResult(GuardMetricsTags.RESULT_SUCCESS);
      return result;
    } catch (Throwable t) {
      observation.error(t);
      observationContext.setResult(GuardMetricsTags.RESULT_FAILURE);
      throw t;
    } finally {
      observation.stop();
    }
  }

  @Override
  public boolean supports(GuardInvocationContext context) {
    return context.hasAnnotation(RateLimit.class)
        || context.hasAnnotation(Idempotent.class)
        || context.hasAnnotation(DistributedLock.class)
        || context.hasAnnotation(AuditLog.class);
  }

  @Override
  public int getOrder() {
    return ORDER;
  }

  private GuardObservationContext buildObservationContext(GuardInvocationContext context) {
    GuardObservationContext ctx = new GuardObservationContext();
    ctx.setClassName(context.getTargetClass().getSimpleName());
    ctx.setMethodName(context.getMethod().getName());
    ctx.setGuardType(resolveGuardType(context));
    ctx.setProvider("guard");
    return ctx;
  }

  private String resolveGuardType(GuardInvocationContext context) {
    int count = 0;
    if (context.hasAnnotation(RateLimit.class)) count++;
    if (context.hasAnnotation(Idempotent.class)) count++;
    if (context.hasAnnotation(DistributedLock.class)) count++;
    if (context.hasAnnotation(AuditLog.class)) count++;

    if (count > 1) return GuardMetricsTags.TYPE_MULTI;
    if (context.hasAnnotation(RateLimit.class)) return GuardMetricsTags.TYPE_RATE_LIMIT;
    if (context.hasAnnotation(Idempotent.class)) return GuardMetricsTags.TYPE_IDEMPOTENT;
    if (context.hasAnnotation(DistributedLock.class)) return GuardMetricsTags.TYPE_LOCK;
    if (context.hasAnnotation(AuditLog.class)) return GuardMetricsTags.TYPE_AUDIT;
    return GuardMetricsTags.TYPE_MULTI;
  }
}
