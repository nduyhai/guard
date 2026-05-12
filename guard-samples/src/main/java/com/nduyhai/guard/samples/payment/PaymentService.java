package com.nduyhai.guard.samples.payment;

import com.nduyhai.guard.annotation.AuditLog;
import com.nduyhai.guard.annotation.DistributedLock;
import com.nduyhai.guard.annotation.Idempotent;
import com.nduyhai.guard.annotation.RateLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Demo service illustrating all four Guard annotations working together.
 *
 * <p>Execution order for {@link #createPayment}:
 * <ol>
 *   <li>{@code @AuditLog}       — outermost; records result or exception post-execution</li>
 *   <li>{@code @RateLimit}      — rejects if the merchant exceeds 10 calls per minute</li>
 *   <li>{@code @Idempotent}     — replays cached result for duplicate order IDs</li>
 *   <li>{@code @DistributedLock}— serialises concurrent calls for the same order</li>
 *   <li>Business logic          — the actual method body</li>
 * </ol>
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @AuditLog(action = "CREATE_PAYMENT", description = "Process a new payment request")
    @RateLimit(key = "#merchantId", limit = 10, window = "1m")
    @Idempotent(key = "#request.orderId()", ttl = "10m")
    @DistributedLock(key = "'payment:' + #request.orderId()", leaseTime = "30s")
    public PaymentResponse createPayment(PaymentRequest request, String merchantId) {
        log.info("Processing payment for order={} merchant={}", request.orderId(), merchantId);
        // Simulate business logic
        return PaymentResponse.success(request.orderId());
    }
}
