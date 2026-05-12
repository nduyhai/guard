package com.nduyhai.guard.autoconfigure;

import com.nduyhai.guard.aop.GuardAdvisor;
import com.nduyhai.guard.core.idempotent.IdempotentStore;
import com.nduyhai.guard.core.lock.LockProvider;
import com.nduyhai.guard.core.ratelimit.GuardRateLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class GuardAutoConfigurationTests {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GuardAutoConfiguration.class));

    @Test
    void registersAllDefaultBeans() {
        runner.run(ctx -> {
            assertThat(ctx).hasSingleBean(GuardAdvisor.class);
            assertThat(ctx).hasSingleBean(IdempotentStore.class);
            assertThat(ctx).hasSingleBean(LockProvider.class);
            assertThat(ctx).hasSingleBean(GuardRateLimiter.class);
        });
    }

    @Test
    void disabledWhenPropertySetToFalse() {
        runner.withPropertyValues("guard.enabled=false")
                .run(ctx -> assertThat(ctx).doesNotHaveBean(GuardAdvisor.class));
    }

    @Test
    void customIdempotentStoreTakesPrecedence() {
        runner.withBean("customStore", IdempotentStore.class, CustomIdempotentStore::new)
                .run(ctx -> {
                    assertThat(ctx).hasSingleBean(IdempotentStore.class);
                    assertThat(ctx.getBean(IdempotentStore.class)).isInstanceOf(CustomIdempotentStore.class);
                });
    }

    // ---- Stub custom implementation ----

    static class CustomIdempotentStore implements IdempotentStore {
        @Override
        public java.util.Optional<Object> get(String key) {
            return java.util.Optional.empty();
        }

        @Override
        public void put(String key, Object result, java.time.Duration ttl) {
        }
    }
}
