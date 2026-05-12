package com.nduyhai.guard.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nduyhai.guard.annotation.Idempotent;
import com.nduyhai.guard.annotation.RateLimit;
import com.nduyhai.guard.audit.internal.AuditLogHandler;
import com.nduyhai.guard.audit.internal.LoggingAuditPublisher;
import com.nduyhai.guard.config.GuardConfiguration;
import com.nduyhai.guard.core.idempotent.IdempotentStore;
import com.nduyhai.guard.core.lock.LockProvider;
import com.nduyhai.guard.core.ratelimit.GuardRateLimiter;
import com.nduyhai.guard.core.ratelimit.RateLimitExceededException;
import com.nduyhai.guard.idempotent.api.IdempotentKeyResolver;
import com.nduyhai.guard.idempotent.internal.DefaultIdempotentKeyResolver;
import com.nduyhai.guard.idempotent.internal.IdempotentHandler;
import com.nduyhai.guard.idempotent.internal.InMemoryIdempotentStore;
import com.nduyhai.guard.lock.api.LockKeyResolver;
import com.nduyhai.guard.lock.internal.DefaultLockKeyResolver;
import com.nduyhai.guard.lock.internal.DistributedLockHandler;
import com.nduyhai.guard.lock.internal.InMemoryLockProvider;
import com.nduyhai.guard.ratelimit.api.RateLimitKeyResolver;
import com.nduyhai.guard.ratelimit.internal.DefaultRateLimitKeyResolver;
import com.nduyhai.guard.ratelimit.internal.RateLimitHandler;
import com.nduyhai.guard.ratelimit.internal.SlidingWindowRateLimiter;
import com.nduyhai.guard.support.AnnotationMetadataExtractor;
import com.nduyhai.guard.support.SpelExpressionEvaluator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration test verifying that Guard AOP advice is correctly applied.
 * GuardConfiguration only enables @EnableAspectJAutoProxy; all beans are wired manually here
 * (mirroring what GuardAutoConfiguration does in a Spring Boot context).
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GuardMethodInterceptorIntegrationTests.TestConfig.class)
class GuardMethodInterceptorIntegrationTests {

  @Autowired private TestService testService;

  @Test
  void idempotentReturnsFirstResultOnSubsequentCalls() {
    String first = testService.idempotentMethod("key-1");
    String second = testService.idempotentMethod("key-1");
    assertThat(first).isEqualTo(second);
  }

  @Test
  void rateLimitThrowsAfterLimitExceeded() {
    testService.rateLimitedMethod("user-B");
    testService.rateLimitedMethod("user-B");
    assertThatThrownBy(() -> testService.rateLimitedMethod("user-B"))
        .isInstanceOf(RateLimitExceededException.class);
  }

  // ---- Test configuration ----

  @Configuration
  @Import(GuardConfiguration.class) // enables @EnableAspectJAutoProxy
  static class TestConfig {

    @Bean SpelExpressionEvaluator spelExpressionEvaluator() { return new SpelExpressionEvaluator(); }
    @Bean AnnotationMetadataExtractor annotationMetadataExtractor() { return new AnnotationMetadataExtractor(); }

    @Bean IdempotentStore idempotentStore() { return new InMemoryIdempotentStore(); }
    @Bean IdempotentKeyResolver idempotentKeyResolver(SpelExpressionEvaluator e) { return new DefaultIdempotentKeyResolver(e); }
    @Bean IdempotentHandler idempotentHandler(IdempotentStore s, IdempotentKeyResolver r) { return new IdempotentHandler(s, r); }

    @Bean LockProvider lockProvider() { return new InMemoryLockProvider(); }
    @Bean LockKeyResolver lockKeyResolver(SpelExpressionEvaluator e) { return new DefaultLockKeyResolver(e); }
    @Bean DistributedLockHandler distributedLockHandler(LockProvider p, LockKeyResolver r) { return new DistributedLockHandler(p, r); }

    @Bean GuardRateLimiter rateLimiter() { return new SlidingWindowRateLimiter(); }
    @Bean RateLimitKeyResolver rateLimitKeyResolver(SpelExpressionEvaluator e) { return new DefaultRateLimitKeyResolver(e); }
    @Bean RateLimitHandler rateLimitHandler(GuardRateLimiter l, RateLimitKeyResolver r) { return new RateLimitHandler(l, r); }

    @Bean AuditLogHandler auditLogHandler() { return new AuditLogHandler(new LoggingAuditPublisher()); }

    // AOP chain — wired manually (GuardAutoConfiguration does this in Boot context)
    @Bean GuardExecutionChain guardExecutionChain(
        IdempotentHandler i, DistributedLockHandler l, RateLimitHandler r, AuditLogHandler a) {
      return new GuardExecutionChain(List.of(i, l, r, a));
    }
    @Bean GuardMethodInterceptor guardMethodInterceptor(GuardExecutionChain c, AnnotationMetadataExtractor e) {
      return new GuardMethodInterceptor(c, e);
    }
    @Bean GuardAdvisor guardAdvisor(GuardMethodInterceptor i) { return new GuardAdvisor(i); }

    @Bean TestService testService() { return new TestService(); }
  }

  static class TestService {
    private int counter = 0;

    @Idempotent(key = "#id", ttl = "5m")
    public String idempotentMethod(String id) { return "result-" + (++counter); }

    @RateLimit(key = "#userId", limit = 2, window = "1m")
    public String rateLimitedMethod(String userId) { return "ok"; }
  }
}
