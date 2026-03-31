package com.volpe.service;

import com.volpe.dto.RevenueRequest;
import com.volpe.dto.RevenueResponse;
import com.volpe.repository.ParkingSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevenueServiceTest {

    @Mock
    private ParkingSessionRepository sessionRepository;

    @InjectMocks
    private RevenueService revenueService;

    @Test
    void shouldReturnRevenueForSectorAndDate() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        RevenueRequest request = new RevenueRequest(date, "A");

        when(sessionRepository.sumAmountBySectorAndDate(eq("A"), any(Instant.class), any(Instant.class)))
                .thenReturn(new BigDecimal("150.00"));

        RevenueResponse response = revenueService.getRevenue(request);

        assertEquals(new BigDecimal("150.00"), response.amount());
        assertEquals("BRL", response.currency());
    }

    @Test
    void shouldReturnZeroWhenNoCompletedSessions() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        RevenueRequest request = new RevenueRequest(date, "B");

        when(sessionRepository.sumAmountBySectorAndDate(eq("B"), any(Instant.class), any(Instant.class)))
                .thenReturn(BigDecimal.ZERO);

        RevenueResponse response = revenueService.getRevenue(request);

        assertEquals(BigDecimal.ZERO, response.amount());
    }

    @Test
    void shouldReturnEndOfDayTimestampInUtc() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        RevenueRequest request = new RevenueRequest(date, "A");

        when(sessionRepository.sumAmountBySectorAndDate(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        RevenueResponse response = revenueService.getRevenue(request);

        assertEquals("2025-01-01T23:59:59Z", response.timestamp().toString());
    }
}
