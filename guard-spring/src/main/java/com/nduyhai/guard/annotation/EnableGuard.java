package com.nduyhai.guard.annotation;

import com.nduyhai.guard.config.GuardConfigurationSelector;
import java.lang.annotation.*;
import org.springframework.context.annotation.Import;

/**
 * Enables the Guard annotation-driven method protection framework.
 *
 * <p>Add to any {@code @Configuration} class to activate {@code @Idempotent},
 * {@code @DistributedLock}, {@code @RateLimit}, and {@code @AuditLog} processing.
 *
 * <p>When using Spring Boot, the {@code guard-spring-boot-starter} activates Guard automatically
 * via auto-configuration; {@code @EnableGuard} is then optional.
 *
 * <pre>{@code
 * @EnableGuard
 * @SpringBootApplication
 * public class MyApplication { }
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(GuardConfigurationSelector.class)
public @interface EnableGuard {}
