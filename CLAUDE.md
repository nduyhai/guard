# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

- **Java 25**, **Spring Boot 4.0.6**, **Spring Framework 7**
- **Maven multi-module** (wrapper included — use `./mvnw`)
- **Spring Modulith** for module boundary verification
- **Jakarta EE 11** (`jakarta.*` imports only — never `javax.*`)

## Commands

```bash
# Build all modules (skipping tests)
./mvnw clean install -DskipTests

# Full build with tests
./mvnw clean verify

# Run a single test class across all modules
./mvnw test -Dtest=GuardExecutionChainTests

# Run a single test method
./mvnw test -Dtest=GuardUtilsTests#parsesShorthandDurations

# Run tests in a specific module
./mvnw test -pl guard-spring

# Run the sample application
./mvnw spring-boot:run -pl guard-samples

# Build with native image hints validation (AOT)
./mvnw spring-boot:process-aot -pl guard-samples
```

## Module responsibilities

| Module | Package | Purpose |
|---|---|---|
| `guard-core` | `com.nduyhai.guard.core` | Pure Java SPI — zero Spring dependency. All contracts live here. |
| `guard-spring` | `com.nduyhai.guard` | Spring AOP implementation, annotations, handlers, Spring Modulith modules. |
| `guard-spring-boot-autoconfigure` | `com.nduyhai.guard.autoconfigure` | `@AutoConfiguration`, `@ConditionalOnMissingBean` defaults, `RuntimeHintsRegistrar`. |
| `guard-spring-boot-starter` | — | Dependency aggregator only — no Java source. |
| `guard-samples` | `com.nduyhai.guard.samples` | Runnable demo: REST controller + all four guards combined. |

## Architecture: how a method call flows

1. `GuardAdvisor` (a Spring `PointcutAdvisor`) matches any method annotated with `@Idempotent`, `@DistributedLock`, `@RateLimit`, or `@AuditLog`.
2. `GuardMethodInterceptor` intercepts the call and builds a `GuardInvocationContext` (method, args, target class, resolved annotations).
3. `GuardExecutionChain` filters the registered `GuardHandler` beans by `supports()` and sorts them ascending by `getOrder()`. Lower order = outermost wrapper.
4. The chain executes: **AuditLogHandler (50) → RateLimitHandler (100) → IdempotentHandler (200) → DistributedLockHandler (300) → business method**.
5. Each handler receives a `GuardOperationInvoker` callback representing "the rest of the chain"; calling `invoker.invoke()` proceeds to the next step.

## Key design decisions

- **No field injection** — constructor injection everywhere.
- **`@ConditionalOnMissingBean`** on every auto-configured bean so users can replace any SPI implementation by declaring their own bean.
- **`@EnableGuard`** imports `GuardConfigurationSelector` → `GuardConfiguration`, which registers the advisor. Auto-configuration also registers everything; `@EnableGuard` is optional in Boot apps.
- **SpEL keys** — `@Idempotent(key = "#request.orderId()")` is evaluated by `SpelExpressionEvaluator` using `MethodBasedEvaluationContext` so parameter names are available.
- **Duration syntax** — annotation attributes like `ttl = "10m"` are parsed by `GuardUtils.parseDuration()` (supports `s/m/h/d` shorthand and ISO-8601).
- **Virtual-thread friendly** — `SlidingWindowRateLimiter` and `InMemoryLockProvider` use per-key `ReentrantLock` instead of `synchronized` on shared monitors.

## Spring Modulith structure (inside `guard-spring`)

Each domain has its own sub-package with `api/` (public SPI) and `internal/` (private impl):

```
com.nduyhai.guard.idempotent   @ApplicationModule — IdempotentKeyResolver (api), IdempotentHandler (internal)
com.nduyhai.guard.lock         @ApplicationModule — LockKeyResolver (api), DistributedLockHandler (internal)
com.nduyhai.guard.ratelimit    @ApplicationModule — RateLimitKeyResolver (api), RateLimitHandler (internal)
com.nduyhai.guard.audit        @ApplicationModule — AuditLogHandler (internal)
```

`internal` classes must not be referenced directly from other modules or application code.

## SPI extension pattern

All SPIs are in `guard-core` (no Spring). Replace any default bean:

```java
@Bean
public IdempotentStore redisIdempotentStore(StringRedisTemplate redis) { ... }

@Bean
public LockProvider redisLockProvider(RedisConnectionFactory cf) { ... }

@Bean
public AuditPublisher kafkaAuditPublisher(...) { ... }
```

Add a custom `GuardHandler` bean (implementing `GuardHandler` with a unique `getOrder()`) to introduce new guard behaviours without touching existing code.
