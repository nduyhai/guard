/**
 * Guard audit-log module.
 *
 * <p>Internal implementation details are under {@code internal}. The publisher SPI ({@link
 * com.nduyhai.guard.core.audit.AuditPublisher}) lives in {@code guard-core} so downstream modules
 * can depend on it without pulling in Spring infrastructure.
 */
@org.springframework.modulith.ApplicationModule(allowedDependencies = {"shared", "support"})
package com.nduyhai.guard.audit;
