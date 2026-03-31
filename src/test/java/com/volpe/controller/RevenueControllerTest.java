package com.volpe.controller;

import com.volpe.dto.RevenueResponse;
import com.volpe.service.RevenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RevenueController.class)
class RevenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RevenueService revenueService;

    @Test
    void shouldReturnRevenueOnValidRequest() throws Exception {
        when(revenueService.getRevenue(any()))
                .thenReturn(new RevenueResponse(new BigDecimal("150.00"), "BRL", Instant.parse("2025-01-01T23:59:59Z")));

        mockMvc.perform(get("/revenue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2025-01-01","sector":"A"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.currency").value("BRL"))
                .andExpect(jsonPath("$.timestamp").value("2025-01-01T23:59:59Z"));
    }

    @Test
    void shouldReturnBadRequestWhenDateIsMissing() throws Exception {
        mockMvc.perform(get("/revenue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sector":"A"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenSectorIsBlank() throws Exception {
        mockMvc.perform(get("/revenue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2025-01-01","sector":""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnZeroRevenueWhenNoSessionsExist() throws Exception {
        when(revenueService.getRevenue(any()))
                .thenReturn(new RevenueResponse(BigDecimal.ZERO, "BRL", Instant.parse("2025-01-01T23:59:59Z")));

        mockMvc.perform(get("/revenue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2025-01-01","sector":"B"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(0));
    }
}
