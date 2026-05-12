package com.nduyhai.guard.support;

import org.springframework.beans.factory.BeanFactory;
import org.jspecify.annotations.Nullable;

/**
 * Resolves bean instances by name from a {@link BeanFactory}.
 *
 * <p>Used by handlers that support configurable strategy beans referenced by name in annotation
 * attributes (e.g. a custom {@code IdempotentStore} bean).
 */
public final class BeanNameResolver {

  private final BeanFactory beanFactory;

  public BeanNameResolver(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  /**
   * Returns the bean with the given {@code name} and {@code type}, or {@code null} if {@code name}
   * is blank.
   */
  @Nullable
  public <T> T resolve(@Nullable String name, Class<T> type) {
    if (name == null || name.isBlank()) {
      return null;
    }
    return beanFactory.getBean(name, type);
  }
}
