package com.nduyhai.guard.config;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link DeferredImportSelector} activated by {@code @EnableGuard}.
 *
 * <p>Using a deferred selector ensures Guard's beans are registered after all user-defined
 * {@code @Configuration} classes, so {@code @ConditionalOnMissingBean} checks in auto-configuration
 * work correctly.
 */
public final class GuardConfigurationSelector implements DeferredImportSelector {

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    return new String[] {GuardConfiguration.class.getName()};
  }
}
