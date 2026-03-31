package com.volpe.service;

import com.volpe.domain.Sector;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PricingService {

	public BigDecimal calculateDynamicPrice(final Sector sector, final long activeSessions) {
		double occupancyRate = (double) activeSessions / sector.getMaxCapacity();

		BigDecimal multiplier;
		if (occupancyRate < 0.25) {
			multiplier = new BigDecimal("0.90");
		} else if (occupancyRate < 0.50) {
			multiplier = BigDecimal.ONE;
		} else if (occupancyRate < 0.75) {
			multiplier = new BigDecimal("1.10");
		} else {
			multiplier = new BigDecimal("1.25");
		}

		return sector.getBasePrice().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
	}

	public BigDecimal calculateAmount(final BigDecimal priceApplied, final Instant entryTime, final Instant exitTime) {
		if (priceApplied == null) {
			return BigDecimal.ZERO;
		}

		long minutes = ChronoUnit.MINUTES.between(entryTime, exitTime);

		if (minutes <= 30) {
			return BigDecimal.ZERO;
		}

		long hours = Math.max(1, (long) Math.ceil(minutes / 60.0));

		return priceApplied.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);
	}
}
