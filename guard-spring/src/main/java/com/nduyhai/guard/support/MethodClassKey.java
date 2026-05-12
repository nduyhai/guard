package com.nduyhai.guard.support;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Composite cache key combining a {@link Method} and its declaring/target {@link Class}.
 *
 * <p>Necessary because the same {@code Method} object may be invoked on different proxy subclasses;
 * including the target class avoids cross-contamination in caches.
 */
public final class MethodClassKey {

  private final Method method;
  private final Class<?> targetClass;

  public MethodClassKey(Method method, Class<?> targetClass) {
    this.method = Objects.requireNonNull(method, "method");
    this.targetClass = Objects.requireNonNull(targetClass, "targetClass");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MethodClassKey other)) return false;
    return method.equals(other.method) && targetClass.equals(other.targetClass);
  }

  @Override
  public int hashCode() {
    return 31 * method.hashCode() + targetClass.hashCode();
  }

  @Override
  public String toString() {
    return targetClass.getSimpleName() + "#" + method.getName();
  }
}
