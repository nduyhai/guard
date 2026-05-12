package com.nduyhai.guard.aop;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GuardExecutionChainTests {

    @Test
    void executesHandlersInOrderAscending() throws Throwable {
        List<Integer> executionOrder = new ArrayList<>();

        GuardHandler h1 = recordingHandler(100, executionOrder);
        GuardHandler h2 = recordingHandler(50, executionOrder);
        GuardHandler h3 = recordingHandler(200, executionOrder);

        GuardExecutionChain chain = new GuardExecutionChain(List.of(h1, h2, h3));
        GuardInvocationContext ctx = fakeContext();

        chain.execute(ctx, () -> {
            executionOrder.add(999);
            return "result";
        });

        // order 50 → 100 → 200 → terminal (999)
        assertThat(executionOrder).containsExactly(50, 100, 200, 999);
    }

    @Test
    void skipsHandlersThatDoNotSupport() throws Throwable {
        List<Integer> called = new ArrayList<>();

        GuardHandler supporting = recordingHandler(1, called);
        GuardHandler notSupporting = new GuardHandler() {
            @Override
            public Object handle(GuardInvocationContext context, GuardOperationInvoker invoker) throws Throwable {
                called.add(999);
                return invoker.invoke();
            }

            @Override
            public boolean supports(GuardInvocationContext context) {
                return false;
            }

            @Override
            public int getOrder() {
                return 0;
            }
        };

        GuardExecutionChain chain = new GuardExecutionChain(List.of(supporting, notSupporting));
        chain.execute(fakeContext(), () -> null);

        assertThat(called).containsExactly(1);
    }

    @Test
    void propagatesExceptionFromTerminal() {
        GuardExecutionChain chain = new GuardExecutionChain(List.of());

        assertThatThrownBy(() -> chain.execute(fakeContext(), () -> {
            throw new RuntimeException("boom");
        })).isInstanceOf(RuntimeException.class).hasMessage("boom");
    }

    // ---- helpers ----

    private static GuardHandler recordingHandler(int order, List<Integer> log) {
        return new GuardHandler() {
            @Override
            public Object handle(GuardInvocationContext context, GuardOperationInvoker invoker) throws Throwable {
                log.add(order);
                return invoker.invoke();
            }

            @Override
            public boolean supports(GuardInvocationContext context) {
                return true;
            }

            @Override
            public int getOrder() {
                return order;
            }
        };
    }

    private static GuardInvocationContext fakeContext() throws Exception {
        Method method = GuardExecutionChainTests.class.getDeclaredMethod("fakeContext");
        return new GuardInvocationContext(method, new Object[0],
                new Object(), GuardExecutionChainTests.class, Map.of());
    }
}
