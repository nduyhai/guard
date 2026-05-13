package com.nduyhai.guard.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.nduyhai.guard.annotation.Idempotent;
import com.nduyhai.guard.annotation.RateLimit;
import com.nduyhai.guard.aop.GuardInvocationContext;
import com.nduyhai.guard.aop.GuardOperationInvoker;
import io.micrometer.observation.ObservationRegistry;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GuardObservationHandlerTests {

  private GuardObservationHandler handler;

  @BeforeEach
  void setUp() {
    // Use the NOOP registry — this tests the handler plumbing without a real tracer.
    handler = new GuardObservationHandler(ObservationRegistry.NOOP, null);
  }

  @Test
  void handle_invokesDownstreamAndReturnsResult() throws Throwable {
    GuardInvocationContext context = buildContext(Map.of(RateLimit.class, mock(RateLimit.class)));
    GuardOperationInvoker invoker = () -> "response";

    Object result = handler.handle(context, invoker);

    assertThat(result).isEqualTo("response");
  }

  @Test
  void handle_propagatesException() {
    GuardInvocationContext context = buildContext(Map.of(RateLimit.class, mock(RateLimit.class)));
    GuardOperationInvoker invoker =
        () -> {
          throw new IllegalStateException("downstream error");
        };

    assertThatThrownBy(() -> handler.handle(context, invoker))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("downstream error");
  }

  @Test
  void handle_callsDownstreamExactlyOnce() throws Throwable {
    GuardInvocationContext context = buildContext(Map.of(Idempotent.class, mock(Idempotent.class)));
    AtomicBoolean called = new AtomicBoolean(false);
    GuardOperationInvoker invoker =
        () -> {
          assertThat(called.getAndSet(true)).isFalse();
          return "ok";
        };

    handler.handle(context, invoker);

    assertThat(called).isTrue();
  }

  @Test
  void supports_trueForRateLimit() {
    assertThat(handler.supports(buildContext(Map.of(RateLimit.class, mock(RateLimit.class)))))
        .isTrue();
  }

  @Test
  void supports_trueForIdempotent() {
    assertThat(handler.supports(buildContext(Map.of(Idempotent.class, mock(Idempotent.class)))))
        .isTrue();
  }

  @Test
  void supports_falseWhenNoGuardAnnotations() {
    assertThat(handler.supports(buildContext(Map.of()))).isFalse();
  }

  @Test
  void getOrder_runsBeforeAuditLogHandler() {
    assertThat(handler.getOrder()).isLessThan(50);
  }

  @Test
  void customConvention_isAccepted() {
    GuardObservationConvention custom = mock(GuardObservationConvention.class);
    GuardObservationHandler handlerWithCustom =
        new GuardObservationHandler(ObservationRegistry.NOOP, custom);

    assertThat(handlerWithCustom).isNotNull();
  }

  // ---- Helpers ----

  private GuardInvocationContext buildContext(
      Map<Class<? extends Annotation>, Annotation> annotations) {
    try {
      Method method = SampleTarget.class.getDeclaredMethod("sampleMethod");
      return new GuardInvocationContext(
          method, new Object[0], new SampleTarget(), SampleTarget.class, annotations);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  static class SampleTarget {
    public void sampleMethod() {}
  }
}
