package com.nduyhai.guard.aop;

import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Immutable snapshot of a single method invocation captured by {@link GuardMethodInterceptor}.
 *
 * <p>Holds all information handlers need: the reflected method, arguments, target object, and the
 * guard annotations resolved at interception time.
 */
public final class GuardInvocationContext {

  private final Method method;
  private final Object[] args;
  private final Object target;
  private final Class<?> targetClass;
  private final Map<Class<? extends Annotation>, Annotation> annotations;

  public GuardInvocationContext(
      Method method,
      Object[] args,
      Object target,
      Class<?> targetClass,
      Map<Class<? extends Annotation>, Annotation> annotations) {
    this.method = method;
    this.args = args;
    this.target = target;
    this.targetClass = targetClass;
    this.annotations = Map.copyOf(annotations);
  }

  public Method getMethod() {
    return method;
  }

  public Object[] getArgs() {
    return args;
  }

  public Object getTarget() {
    return target;
  }

  public Class<?> getTargetClass() {
    return targetClass;
  }

  /** Returns the annotation of {@code type} if present on the method or its declaring class. */
  @Nullable
  @SuppressWarnings("unchecked")
  public <A extends Annotation> A getAnnotation(Class<A> type) {
    return (A) annotations.get(type);
  }

  public boolean hasAnnotation(Class<? extends Annotation> type) {
    return annotations.containsKey(type);
  }

  /** Fully-qualified {@code ClassName#methodName} identifier for logging and auditing. */
  public String getQualifiedMethodName() {
    return targetClass.getSimpleName() + "#" + method.getName();
  }
}
