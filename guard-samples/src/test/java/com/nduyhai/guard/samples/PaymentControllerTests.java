package com.nduyhai.guard.samples;

import com.nduyhai.guard.samples.payment.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPaymentReturnsSuccess() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .param("merchantId", "merchant-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId":"order-999","amount":5000,"currency":"USD"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.orderId").value("order-999"));
    }

    @Test
    void duplicateOrderIdReturnsCachedResult() throws Exception {
        String body = """
                {"orderId":"order-idempotent","amount":100,"currency":"EUR"}
                """;

        mockMvc.perform(post("/api/payments")
                        .param("merchantId", "merchant-002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        // Second call with same orderId must return 200 (cached)
        mockMvc.perform(post("/api/payments")
                        .param("merchantId", "merchant-002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
