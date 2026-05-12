package com.nduyhai.guard.samples.payment;

import com.nduyhai.guard.core.lock.LockException;
import com.nduyhai.guard.core.ratelimit.RateLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller demonstrating Guard-protected endpoints.
 *
 * <pre>
 * POST /api/payments?merchantId=merchant-001
 * {
 *   "orderId": "order-123",
 *   "amount": 9999,
 *   "currency": "USD"
 * }
 * </pre>
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping
  public ResponseEntity<PaymentResponse> createPayment(
      @RequestBody PaymentRequest request, @RequestParam String merchantId) {
    PaymentResponse response = paymentService.createPayment(request, merchantId);
    return ResponseEntity.ok(response);
  }

  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<ErrorResponse> handleRateLimit(RateLimitExceededException ex) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(new ErrorResponse("RATE_LIMIT_EXCEEDED", ex.getMessage()));
  }

  @ExceptionHandler(LockException.class)
  public ResponseEntity<ErrorResponse> handleLock(LockException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse("LOCK_CONFLICT", ex.getMessage()));
  }

  public record ErrorResponse(String code, String message) {}
}
