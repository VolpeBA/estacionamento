package com.volpe.service;

import com.volpe.domain.Sector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    private PricingService pricingService;
    private Sector sector;

    @BeforeEach
    void setUp() {
        pricingService = new PricingService();
        sector = new Sector();
        sector.setBasePrice(new BigDecimal("10.00"));
        sector.setMaxCapacity(100);
    }

    @Test
    void shouldApplyTenPercentDiscountWhenOccupancyBelow25Percent() {
        BigDecimal price = pricingService.calculateDynamicPrice(sector, 10);
        assertEquals(new BigDecimal("9.00"), price);
    }

    @Test
    void shouldApplyNoPriceChangeWhenOccupancyBetween25And50Percent() {
        BigDecimal price = pricingService.calculateDynamicPrice(sector, 30);
        assertEquals(new BigDecimal("10.00"), price);
    }

    @Test
    void shouldApplyTenPercentSurchargeWhenOccupancyBetween50And75Percent() {
        BigDecimal price = pricingService.calculateDynamicPrice(sector, 60);
        assertEquals(new BigDecimal("11.00"), price);
    }

    @Test
    void shouldApply25PercentSurchargeWhenOccupancyAbove75Percent() {
        BigDecimal price = pricingService.calculateDynamicPrice(sector, 80);
        assertEquals(new BigDecimal("12.50"), price);
    }

    @Test
    void shouldApply25PercentSurchargeWhenSectorIsFull() {
        BigDecimal price = pricingService.calculateDynamicPrice(sector, 100);
        assertEquals(new BigDecimal("12.50"), price);
    }

    @Test
    void shouldReturnZeroAmountWhenParkedWithin30Minutes() {
        Instant entry = Instant.now();
        Instant exit = entry.plus(25, ChronoUnit.MINUTES);

        BigDecimal amount = pricingService.calculateAmount(new BigDecimal("10.00"), entry, exit);

        assertEquals(BigDecimal.ZERO, amount);
    }

    @Test
    void shouldReturnZeroAmountWhenParkedExactly30Minutes() {
        Instant entry = Instant.now();
        Instant exit = entry.plus(30, ChronoUnit.MINUTES);

        BigDecimal amount = pricingService.calculateAmount(new BigDecimal("10.00"), entry, exit);

        assertEquals(BigDecimal.ZERO, amount);
    }

    @Test
    void shouldChargeOneHourMinimumAfterTolerance() {
        Instant entry = Instant.now();
        Instant exit = entry.plus(31, ChronoUnit.MINUTES);

        BigDecimal amount = pricingService.calculateAmount(new BigDecimal("10.00"), entry, exit);

        assertEquals(new BigDecimal("10.00"), amount);
    }

    @Test
    void shouldRoundUpToNextHour() {
        Instant entry = Instant.now();
        Instant exit = entry.plus(61, ChronoUnit.MINUTES);

        BigDecimal amount = pricingService.calculateAmount(new BigDecimal("10.00"), entry, exit);

        assertEquals(new BigDecimal("20.00"), amount);
    }

    @Test
    void shouldChargeExactHoursWithoutRounding() {
        Instant entry = Instant.now();
        Instant exit = entry.plus(120, ChronoUnit.MINUTES);

        BigDecimal amount = pricingService.calculateAmount(new BigDecimal("10.00"), entry, exit);

        assertEquals(new BigDecimal("20.00"), amount);
    }

    @Test
    void shouldReturnZeroWhenPriceAppliedIsNull() {
        Instant entry = Instant.now();
        Instant exit = entry.plus(90, ChronoUnit.MINUTES);

        BigDecimal amount = pricingService.calculateAmount(null, entry, exit);

        assertEquals(BigDecimal.ZERO, amount);
    }
}
