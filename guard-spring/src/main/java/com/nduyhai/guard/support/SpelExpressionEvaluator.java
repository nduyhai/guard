package com.nduyhai.guard.support;

import com.nduyhai.guard.aop.GuardInvocationContext;
import org.jspecify.annotations.Nullable;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Evaluates SpEL expressions declared in Guard annotations against a {@link
 * GuardInvocationContext}.
 *
 * <p>Method parameters are exposed by name (e.g. {@code #orderId}) and the root object is the bean
 * that owns the annotated method, allowing access to {@code #root.method} etc.
 */
public final class SpelExpressionEvaluator {

  private final ExpressionParser parser = new SpelExpressionParser();
  private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

  /**
   * Evaluates {@code expression} and returns the result as a {@link String}.
   *
   * @param expression a SpEL expression string, e.g. {@code "#request.orderId"}
   * @param context the current invocation context
   * @return the evaluated string value
   */
  @Nullable
  public String evaluate(String expression, GuardInvocationContext context) {
    if (expression == null || expression.isBlank()) {
      return null;
    }
    StandardEvaluationContext evalContext =
        new MethodBasedEvaluationContext(
            context.getTarget(), context.getMethod(), context.getArgs(), nameDiscoverer);
    return parser.parseExpression(expression).getValue(evalContext, String.class);
  }

  /** Evaluates {@code expression} and returns the result as the specified {@code type}. */
  @Nullable
  public <T> T evaluate(String expression, GuardInvocationContext context, Class<T> type) {
    if (expression == null || expression.isBlank()) {
      return null;
    }
    StandardEvaluationContext evalContext =
        new MethodBasedEvaluationContext(
            context.getTarget(), context.getMethod(), context.getArgs(), nameDiscoverer);
    return parser.parseExpression(expression).getValue(evalContext, type);
  }
}
