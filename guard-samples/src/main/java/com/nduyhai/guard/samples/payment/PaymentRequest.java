package com.nduyhai.guard.samples.payment;

/**
 * Sample payment request used by the demo REST layer.
 *
 * @param orderId    unique order identifier used as idempotency / lock key
 * @param amount     payment amount in the smallest currency unit (e.g. cents)
 * @param currency   ISO 4217 currency code
 */
public record PaymentRequest(String orderId, long amount, String currency) {
}
