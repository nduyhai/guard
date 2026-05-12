package com.nduyhai.guard.aop;

import com.nduyhai.guard.annotation.AuditLog;
import com.nduyhai.guard.annotation.DistributedLock;
import com.nduyhai.guard.annotation.Idempotent;
import com.nduyhai.guard.annotation.RateLimit;
import java.lang.annotation.Annotation;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.core.Ordered;

/**
 * Spring AOP {@link PointcutAdvisor} that applies {@link GuardMethodInterceptor} to any method or
 * class annotated with a Guard annotation.
 *
 * <p>The pointcut is the union of four {@link AnnotationMatchingPointcut}s (one per annotation),
 * each checking both method and class level. Order is {@code Ordered.LOWEST_PRECEDENCE - 100} so
 * Guard runs after transaction management but before most application advisors.
 */
public final class GuardAdvisor implements PointcutAdvisor, Ordered {

  public static final int ORDER = Ordered.LOWEST_PRECEDENCE - 100;

  private final GuardMethodInterceptor interceptor;
  private final Pointcut pointcut;

  public GuardAdvisor(GuardMethodInterceptor interceptor) {
    this.interceptor = interceptor;
    this.pointcut = buildPointcut();
  }

  private static Pointcut buildPointcut() {
    return new ComposablePointcut(annotationPointcut(Idempotent.class))
        .union(annotationPointcut(DistributedLock.class))
        .union(annotationPointcut(RateLimit.class))
        .union(annotationPointcut(AuditLog.class));
  }

  private static Pointcut annotationPointcut(Class<? extends Annotation> annotationType) {
    // checkInherited=true so annotations on interfaces/superclasses are detected
    return new AnnotationMatchingPointcut(null, annotationType, true);
  }

  @Override
  public Pointcut getPointcut() {
    return pointcut;
  }

  @Override
  public Advice getAdvice() {
    return interceptor;
  }

  @Override
  public boolean isPerInstance() {
    return true;
  }

  @Override
  public int getOrder() {
    return ORDER;
  }
}
