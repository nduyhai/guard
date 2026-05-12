/**
 * Guard Spring — annotation-driven method-level guard framework built on Spring AOP.
 *
 * <p>Activate by placing {@code @EnableGuard} on a {@code @Configuration} class, or rely on Spring
 * Boot auto-configuration via {@code guard-spring-boot-starter}.
 *
 * <h2>Spring Modulith modules</h2>
 *
 * <ul>
 *   <li>{@code com.nduyhai.guard.idempotent} — idempotency handling
 *   <li>{@code com.nduyhai.guard.lock} — distributed locking
 *   <li>{@code com.nduyhai.guard.ratelimit} — rate limiting
 *   <li>{@code com.nduyhai.guard.audit} — audit logging
 * </ul>
 */
package com.nduyhai.guard;
