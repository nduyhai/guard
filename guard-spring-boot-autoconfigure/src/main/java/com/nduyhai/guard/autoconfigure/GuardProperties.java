package com.nduyhai.guard.autoconfigure;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Guard framework, bound from the {@code guard.*} namespace.
 *
 * <pre>{@code
 * guard:
 *   enabled: true
 *   idempotent:
 *     default-ttl: 10m
 *   lock:
 *     default-wait-time: 5s
 *     default-lease-time: 30s
 *   rate-limit:
 *     default-limit: 100
 *     default-window: 1m
 * }</pre>
 */
@ConfigurationProperties(prefix = "guard")
public class GuardProperties {

  private final Idempotent idempotent = new Idempotent();
  private final Lock lock = new Lock();
  private final RateLimit rateLimit = new RateLimit();
  private final Audit audit = new Audit();

  /** Set to {@code false} to disable Guard entirely (no advisor registered). */
  private boolean enabled = true;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Idempotent getIdempotent() {
    return idempotent;
  }

  public Lock getLock() {
    return lock;
  }

  public RateLimit getRateLimit() {
    return rateLimit;
  }

  public Audit getAudit() {
    return audit;
  }

  public static class Idempotent {
    private Duration defaultTtl = Duration.ofMinutes(10);

    public Duration getDefaultTtl() {
      return defaultTtl;
    }

    public void setDefaultTtl(Duration defaultTtl) {
      this.defaultTtl = defaultTtl;
    }
  }

  public static class Lock {
    private Duration defaultWaitTime = Duration.ofSeconds(5);
    private Duration defaultLeaseTime = Duration.ofSeconds(30);

    public Duration getDefaultWaitTime() {
      return defaultWaitTime;
    }

    public void setDefaultWaitTime(Duration defaultWaitTime) {
      this.defaultWaitTime = defaultWaitTime;
    }

    public Duration getDefaultLeaseTime() {
      return defaultLeaseTime;
    }

    public void setDefaultLeaseTime(Duration defaultLeaseTime) {
      this.defaultLeaseTime = defaultLeaseTime;
    }
  }

  public static class RateLimit {
    private long defaultLimit = 100;
    private Duration defaultWindow = Duration.ofMinutes(1);

    public long getDefaultLimit() {
      return defaultLimit;
    }

    public void setDefaultLimit(long defaultLimit) {
      this.defaultLimit = defaultLimit;
    }

    public Duration getDefaultWindow() {
      return defaultWindow;
    }

    public void setDefaultWindow(Duration defaultWindow) {
      this.defaultWindow = defaultWindow;
    }
  }

  public static class Audit {
    private boolean enabled = true;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }
}
