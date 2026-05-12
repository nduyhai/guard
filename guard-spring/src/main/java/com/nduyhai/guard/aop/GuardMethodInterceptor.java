package com.nduyhai.guard.aop;

import com.nduyhai.guard.support.AnnotationMetadataExtractor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.support.AopUtils;

/**
 * AOP {@link MethodInterceptor} that drives the {@link GuardExecutionChain} for every intercepted
 * method call.
 *
 * <p>Wired into {@link GuardAdvisor} so that Spring AOP applies it to all methods bearing at least
 * one Guard annotation.
 */
public final class GuardMethodInterceptor implements MethodInterceptor {

  private final GuardExecutionChain executionChain;
  private final AnnotationMetadataExtractor metadataExtractor;

  public GuardMethodInterceptor(
      GuardExecutionChain executionChain, AnnotationMetadataExtractor metadataExtractor) {
    this.executionChain = executionChain;
    this.metadataExtractor = metadataExtractor;
  }

  @Override
  @Nullable
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Method method = invocation.getMethod();
    Object target = invocation.getThis();
    Class<?> targetClass =
        (target != null) ? AopUtils.getTargetClass(target) : method.getDeclaringClass();

    Map<Class<? extends Annotation>, Annotation> annotations =
        metadataExtractor.extract(method, targetClass);

    if (annotations.isEmpty()) {
      return invocation.proceed();
    }

    GuardInvocationContext context =
        new GuardInvocationContext(
            method, invocation.getArguments(), target, targetClass, annotations);

    return executionChain.execute(context, invocation::proceed);
  }
}
