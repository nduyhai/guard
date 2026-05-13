package com.nduyhai.guard.autoconfigure;

import com.nduyhai.guard.aop.GuardAdvisor;
import com.nduyhai.guard.aop.GuardExecutionChain;
import com.nduyhai.guard.aop.GuardHandler;
import com.nduyhai.guard.aop.GuardMethodInterceptor;
import com.nduyhai.guard.audit.internal.AuditLogHandler;
import com.nduyhai.guard.audit.internal.LoggingAuditPublisher;
import com.nduyhai.guard.core.audit.AuditPublisher;
import com.nduyhai.guard.core.idempotent.IdempotentStore;
import com.nduyhai.guard.core.lock.LockProvider;
import com.nduyhai.guard.core.ratelimit.GuardRateLimiter;
import com.nduyhai.guard.idempotent.api.IdempotentKeyResolver;
import com.nduyhai.guard.idempotent.internal.DefaultIdempotentKeyResolver;
import com.nduyhai.guard.idempotent.internal.IdempotentHandler;
import com.nduyhai.guard.idempotent.internal.InMemoryIdempotentStore;
import com.nduyhai.guard.lock.api.LockKeyResolver;
import com.nduyhai.guard.lock.internal.DefaultLockKeyResolver;
import com.nduyhai.guard.lock.internal.DistributedLockHandler;
import com.nduyhai.guard.lock.internal.InMemoryLockProvider;
import com.nduyhai.guard.metrics.GuardMetrics;
import com.nduyhai.guard.metrics.NoopGuardMetrics;
import com.nduyhai.guard.ratelimit.api.RateLimitKeyResolver;
import com.nduyhai.guard.ratelimit.internal.DefaultRateLimitKeyResolver;
import com.nduyhai.guard.ratelimit.internal.RateLimitHandler;
import com.nduyhai.guard.ratelimit.internal.SlidingWindowRateLimiter;
import com.nduyhai.guard.support.AnnotationMetadataExtractor;
import com.nduyhai.guard.support.SpelExpressionEvaluator;
import java.util.List;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Spring Boot auto-configuration for the Guard framework.
 *
 * <p>Activates when {@code guard.enabled=true} (the default). Registers sensible in-memory default
 * implementations for all SPIs; replace any bean with a custom implementation to override behaviour
 * (e.g. Redis-backed store or custom publisher).
 *
 * <p>All beans are guarded with {@code @ConditionalOnMissingBean} so that
 * {@code @EnableGuard}-registered beans always take precedence.
 *
 * <p>{@link GuardMetricsAutoConfiguration} (which runs before this class) provides a
 * {@link GuardMetrics} bean when Micrometer is on the classpath. When it is absent, the
 * {@link NoopGuardMetrics} fallback registered here is used instead.
 */
@AutoConfiguration
@ConditionalOnProperty(
    prefix = "guard",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties(GuardProperties.class)
@ImportRuntimeHints(GuardRuntimeHints.class)
@RegisterReflectionForBinding(GuardProperties.class)
public class GuardAutoConfiguration {

  // ---- Metrics fallback (used when Micrometer is absent) ----

  @Bean
  @ConditionalOnMissingBean(GuardMetrics.class)
  public NoopGuardMetrics guardMetrics() {
    return new NoopGuardMetrics();
  }

  // ---- SpEL evaluation ----

  @Bean
  @ConditionalOnMissingBean
  public SpelExpressionEvaluator guardSpelExpressionEvaluator() {
    return new SpelExpressionEvaluator();
  }

  // ---- Annotation extraction ----

  @Bean
  @ConditionalOnMissingBean
  public AnnotationMetadataExtractor guardAnnotationMetadataExtractor() {
    return new AnnotationMetadataExtractor();
  }

  // ---- Idempotent ----

  @Bean
  @ConditionalOnMissingBean
  public IdempotentStore guardIdempotentStore() {
    return new InMemoryIdempotentStore();
  }

  @Bean
  @ConditionalOnMissingBean
  public IdempotentKeyResolver guardIdempotentKeyResolver(SpelExpressionEvaluator evaluator) {
    return new DefaultIdempotentKeyResolver(evaluator);
  }

  @Bean
  @ConditionalOnMissingBean
  public IdempotentHandler guardIdempotentHandler(
      IdempotentStore store, IdempotentKeyResolver keyResolver, GuardMetrics guardMetrics) {
    return new IdempotentHandler(store, keyResolver, guardMetrics);
  }

  // ---- Distributed lock ----

  @Bean
  @ConditionalOnMissingBean
  public LockProvider guardLockProvider() {
    return new InMemoryLockProvider();
  }

  @Bean
  @ConditionalOnMissingBean
  public LockKeyResolver guardLockKeyResolver(SpelExpressionEvaluator evaluator) {
    return new DefaultLockKeyResolver(evaluator);
  }

  @Bean
  @ConditionalOnMissingBean
  public DistributedLockHandler guardDistributedLockHandler(
      LockProvider lockProvider, LockKeyResolver keyResolver, GuardMetrics guardMetrics) {
    return new DistributedLockHandler(lockProvider, keyResolver, guardMetrics);
  }

  // ---- Rate limit ----

  @Bean
  @ConditionalOnMissingBean
  public GuardRateLimiter guardRateLimiter() {
    return new SlidingWindowRateLimiter();
  }

  @Bean
  @ConditionalOnMissingBean
  public RateLimitKeyResolver guardRateLimitKeyResolver(SpelExpressionEvaluator evaluator) {
    return new DefaultRateLimitKeyResolver(evaluator);
  }

  @Bean
  @ConditionalOnMissingBean
  public RateLimitHandler guardRateLimitHandler(
      GuardRateLimiter rateLimiter, RateLimitKeyResolver keyResolver, GuardMetrics guardMetrics) {
    return new RateLimitHandler(rateLimiter, keyResolver, guardMetrics);
  }

  // ---- Audit ----

  @Bean
  @ConditionalOnMissingBean
  public AuditPublisher guardAuditPublisher() {
    return new LoggingAuditPublisher();
  }

  @Bean
  @ConditionalOnMissingBean
  public AuditLogHandler guardAuditLogHandler(AuditPublisher publisher, GuardMetrics guardMetrics) {
    return new AuditLogHandler(publisher, guardMetrics);
  }

  // ---- AOP core ----

  @Bean
  @ConditionalOnMissingBean
  public GuardExecutionChain guardExecutionChain(@Autowired List<GuardHandler> handlers) {
    return new GuardExecutionChain(handlers);
  }

  @Bean
  @ConditionalOnMissingBean
  public GuardMethodInterceptor guardMethodInterceptor(
      GuardExecutionChain executionChain, AnnotationMetadataExtractor extractor) {
    return new GuardMethodInterceptor(executionChain, extractor);
  }

  @Bean
  @ConditionalOnMissingBean
  public GuardAdvisor guardAdvisor(GuardMethodInterceptor interceptor) {
    return new GuardAdvisor(interceptor);
  }
}
