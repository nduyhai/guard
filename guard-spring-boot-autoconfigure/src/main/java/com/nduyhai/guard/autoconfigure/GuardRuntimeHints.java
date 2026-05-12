package com.nduyhai.guard.autoconfigure;

import com.nduyhai.guard.annotation.AuditLog;
import com.nduyhai.guard.annotation.DistributedLock;
import com.nduyhai.guard.annotation.Idempotent;
import com.nduyhai.guard.annotation.RateLimit;
import com.nduyhai.guard.aop.GuardAdvisor;
import com.nduyhai.guard.aop.GuardExecutionChain;
import com.nduyhai.guard.aop.GuardMethodInterceptor;
import com.nduyhai.guard.audit.internal.AuditLogHandler;
import com.nduyhai.guard.audit.internal.LoggingAuditPublisher;
import com.nduyhai.guard.idempotent.internal.IdempotentHandler;
import com.nduyhai.guard.idempotent.internal.InMemoryIdempotentStore;
import com.nduyhai.guard.lock.internal.DistributedLockHandler;
import com.nduyhai.guard.lock.internal.InMemoryLockProvider;
import com.nduyhai.guard.ratelimit.internal.RateLimitHandler;
import com.nduyhai.guard.ratelimit.internal.SlidingWindowRateLimiter;
import com.nduyhai.guard.support.AnnotationMetadataExtractor;
import com.nduyhai.guard.support.SpelExpressionEvaluator;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * AOT {@link RuntimeHintsRegistrar} for the Guard framework.
 *
 * <p>Registers all Guard classes that require reflection at native-image compile time.
 */
public final class GuardRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    // Annotations
    hints.reflection().registerType(Idempotent.class, MemberCategory.values());
    hints.reflection().registerType(DistributedLock.class, MemberCategory.values());
    hints.reflection().registerType(RateLimit.class, MemberCategory.values());
    hints.reflection().registerType(AuditLog.class, MemberCategory.values());

    // Core AOP infrastructure
    hints.reflection().registerType(GuardAdvisor.class, MemberCategory.values());
    hints.reflection().registerType(GuardMethodInterceptor.class, MemberCategory.values());
    hints.reflection().registerType(GuardExecutionChain.class, MemberCategory.values());

    // Support
    hints.reflection().registerType(AnnotationMetadataExtractor.class, MemberCategory.values());
    hints.reflection().registerType(SpelExpressionEvaluator.class, MemberCategory.values());

    // Handlers and default implementations
    hints.reflection().registerType(AuditLogHandler.class, MemberCategory.values());
    hints.reflection().registerType(LoggingAuditPublisher.class, MemberCategory.values());
    hints.reflection().registerType(IdempotentHandler.class, MemberCategory.values());
    hints.reflection().registerType(InMemoryIdempotentStore.class, MemberCategory.values());
    hints.reflection().registerType(DistributedLockHandler.class, MemberCategory.values());
    hints.reflection().registerType(InMemoryLockProvider.class, MemberCategory.values());
    hints.reflection().registerType(RateLimitHandler.class, MemberCategory.values());
    hints.reflection().registerType(SlidingWindowRateLimiter.class, MemberCategory.values());
  }
}
