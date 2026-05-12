package com.nduyhai.guard.samples.payment;

/**
 * Sample payment response.
 *
 * @param orderId  echoed order identifier
 * @param status   {@code "SUCCESS"} or {@code "FAILED"}
 * @param message  human-readable detail
 */
public record PaymentResponse(String orderId, String status, String message) {

    public static PaymentResponse success(String orderId) {
        return new PaymentResponse(orderId, "SUCCESS", "Payment processed");
    }
}
