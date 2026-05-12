package com.nduyhai.guard.aop;

import java.util.Comparator;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Builds and executes an ordered chain of {@link GuardHandler}s around a method invocation.
 *
 * <p>Handlers are filtered by {@link GuardHandler#supports(GuardInvocationContext)} and sorted
 * ascending by {@link GuardHandler#getOrder()} so that the handler with the lowest order value
 * becomes the outermost wrapper (first to run, last to return).
 */
public final class GuardExecutionChain {

  private final List<GuardHandler> handlers;

  public GuardExecutionChain(List<GuardHandler> handlers) {
    this.handlers = List.copyOf(handlers);
  }

  /**
   * Executes the guard chain for {@code context}, ultimately invoking {@code terminal} (the actual
   * business method or the next AOP advice).
   */
  @Nullable
  public Object execute(GuardInvocationContext context, GuardOperationInvoker terminal)
      throws Throwable {
    List<GuardHandler> applicable =
        handlers.stream()
            .filter(h -> h.supports(context))
            .sorted(Comparator.comparingInt(GuardHandler::getOrder))
            .toList();

    // Build the chain from innermost to outermost so lower-order handlers wrap all others.
    GuardOperationInvoker chain = terminal;
    for (int i = applicable.size() - 1; i >= 0; i--) {
      final GuardHandler handler = applicable.get(i);
      final GuardOperationInvoker next = chain;
      chain = () -> handler.handle(context, next);
    }
    return chain.invoke();
  }
}
