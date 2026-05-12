/**
 * Guard idempotency module.
 *
 * <p>Public API: {@link com.nduyhai.guard.idempotent.api.IdempotentKeyResolver}. Internal
 * implementation details are under {@code internal} and must not be referenced directly by other
 * modules or application code.
 */
@org.springframework.modulith.ApplicationModule(allowedDependencies = {"shared", "support"})
package com.nduyhai.guard.idempotent;
