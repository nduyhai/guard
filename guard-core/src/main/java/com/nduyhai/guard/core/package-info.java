/**
 * Guard Core — pure Java SPI contracts with no Spring dependency.
 *
 * <p>Sub-packages mirror the four guard domains:
 * <ul>
 *   <li>{@code idempotent} — idempotency store and operation model</li>
 *   <li>{@code lock}       — distributed lock provider and handle</li>
 *   <li>{@code ratelimit}  — rate-limiter and result model</li>
 *   <li>{@code audit}      — audit event and publisher</li>
 * </ul>
 */
package com.nduyhai.guard.core;
