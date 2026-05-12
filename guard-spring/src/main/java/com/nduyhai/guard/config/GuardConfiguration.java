package com.nduyhai.guard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Enables Spring AOP proxy infrastructure for the Guard framework.
 *
 * <p>Imported by {@link GuardConfigurationSelector} (via {@code @EnableGuard}). Contains <em>no
 * bean registrations</em> — all Guard beans are registered exclusively by {@code
 * GuardAutoConfiguration} with {@code @ConditionalOnMissingBean}, which guarantees a single source
 * of truth and prevents duplicate bean-name conflicts when {@code @EnableGuard} is combined with
 * the Spring Boot auto-configuration.
 *
 * <p>In a Spring Boot application, {@link
 * org.springframework.boot.autoconfigure.aop.AopAutoConfiguration} already enables AOP proxying;
 * {@code @EnableAspectJAutoProxy} here is a safety net for non-Boot deployments.
 */
@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy
public class GuardConfiguration {}
