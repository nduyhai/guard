package com.nduyhai.guard.idempotent.internal;

import com.nduyhai.guard.core.idempotent.IdempotentStore;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory {@link IdempotentStore} backed by a {@link ConcurrentHashMap}.
 *
 * <p>Suitable for single-node deployments and testing. For clustered environments, replace with a
 * {@code RedisIdempotentStore} bean.
 *
 * <p>Expired entries are evicted lazily on read. A production deployment should schedule periodic
 * cleanup via {@code @Scheduled} or an external TTL mechanism.
 */
public final class InMemoryIdempotentStore implements IdempotentStore {

  private final ConcurrentHashMap<String, CacheEntry> store = new ConcurrentHashMap<>();

  @Override
  public Optional<Object> get(String key) {
    CacheEntry entry = store.get(key);
    if (entry == null) {
      return Optional.empty();
    }
    if (entry.isExpired()) {
      store.remove(key, entry);
      return Optional.empty();
    }
    return Optional.of(entry.value());
  }

  @Override
  public void put(String key, Object result, Duration ttl) {
    store.put(key, new CacheEntry(result, Instant.now().plus(ttl)));
  }

  /** Returns the number of entries currently in the store (including potentially expired ones). */
  public int size() {
    return store.size();
  }

  private record CacheEntry(Object value, Instant expiresAt) {
    boolean isExpired() {
      return Instant.now().isAfter(expiresAt);
    }
  }
}
