package com.volpe.service;

import com.volpe.dto.RevenueRequest;
import com.volpe.dto.RevenueResponse;
import com.volpe.repository.ParkingSessionRepository;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;

@Service
public class RevenueService {

	private final ParkingSessionRepository sessionRepository;

	public RevenueService(final ParkingSessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	@Transactional(readOnly = true)
	public RevenueResponse getRevenue(@Nonnull final RevenueRequest revenueRequest) {

		final Instant startOfDay = revenueRequest.date().atStartOfDay(ZoneOffset.UTC).toInstant();
		final Instant startOfNextDay = revenueRequest.date().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

		final BigDecimal amount = sessionRepository.sumAmountBySectorAndDate(revenueRequest.sector(), startOfDay, startOfNextDay);

		final Instant timestamp = revenueRequest.date().atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

		return new RevenueResponse(amount, "BRL", timestamp);
	}
}
