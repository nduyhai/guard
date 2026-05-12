package com.nduyhai.guard.samples;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class PaymentControllerTests {

  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void createPaymentReturnsSuccess() throws Exception {
    mockMvc
        .perform(
            post("/api/payments")
                .param("merchantId", "merchant-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"orderId":"order-999","amount":5000,"currency":"USD"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("SUCCESS"))
        .andExpect(jsonPath("$.orderId").value("order-999"));
  }

  @Test
  void duplicateOrderIdReturnsCachedResult() throws Exception {
    String orderId = "order-idem-" + System.nanoTime();
    String body =
        """
        {"orderId":"%s","amount":100,"currency":"EUR"}
        """
            .formatted(orderId);

    mockMvc
        .perform(
            post("/api/payments")
                .param("merchantId", "merchant-002")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/api/payments")
                .param("merchantId", "merchant-002")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("SUCCESS"));
  }
}
