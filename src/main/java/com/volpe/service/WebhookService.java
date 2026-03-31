package com.volpe.service;

import com.volpe.domain.*;
import com.volpe.dto.*;
import com.volpe.exception.NotFoundException;
import com.volpe.exception.SectorFullException;
import com.volpe.repository.EventLogRepository;
import com.volpe.repository.ParkingSessionRepository;
import com.volpe.repository.ParkingSpotRepository;
import com.volpe.repository.SectorRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class WebhookService {

	private final SectorRepository sectorRepository;

	private final ParkingSpotRepository spotRepository;

	private final ParkingSessionRepository sessionRepository;

	private final EventLogRepository eventLogRepository;

	private final PricingService pricingService;

	public WebhookService(final SectorRepository sectorRepository, final ParkingSpotRepository spotRepository, final ParkingSessionRepository sessionRepository, final EventLogRepository eventLogRepository, final PricingService pricingService) {
		this.sectorRepository = sectorRepository;
		this.spotRepository = spotRepository;
		this.sessionRepository = sessionRepository;
		this.eventLogRepository = eventLogRepository;
		this.pricingService = pricingService;
	}

	@Transactional
	public void process(final @NotNull WebhookRequest request) {

		final var event = ParkingEvent.from(request);

		switch (event) {
			case EntryEvent e -> handleEntry(e);
			case ParkedEvent p -> handleParked(p);
			case ExitEvent x -> handleExit(x);
		}
	}

	private void handleEntry(final @NotNull EntryEvent event) {
		final String key = event.licensePlate() + ":ENTRY:" + event.entryTime().toEpochMilli();
		if (eventLogRepository.existsByEventKey(key)) {
			return;
		}

		ParkingSession session = ParkingSession.from(event);

		if (event.sector() != null) {
			Sector sector = sectorRepository.findBySectorName(event.sector()).orElseThrow(() -> new NotFoundException("Sector not found: " + event.sector()));

			long activeCount = countActiveSessions(sector.getId());

			if (activeCount >= sector.getMaxCapacity()) {
				throw new SectorFullException(event.sector());
			}

			BigDecimal price = pricingService.calculateDynamicPrice(sector, activeCount);
			session.setSector(sector);
			session.setPriceApplied(price);
			session.setStatus(SessionStatus.ACTIVE);
		}

		sessionRepository.save(session);
		saveEventLog(key, "ENTRY", event.licensePlate());
	}

	private void handleParked(final ParkedEvent event) {
		String key = event.licensePlate() + ":PARKED:" + event.lat() + ":" + event.lng();
		if (eventLogRepository.existsByEventKey(key)) {
			return;
		}

		ParkingSession session = sessionRepository.findByLicensePlateAndStatusIn(event.licensePlate(), List.of(SessionStatus.PENDING_SPOT, SessionStatus.ACTIVE)).orElseThrow(() -> new NotFoundException("No active parking session found for license plate: " + event.licensePlate()));

		ParkingSpot spot = spotRepository.findByLatAndLng(event.lat(), event.lng()).orElseThrow(() -> new NotFoundException("No parking spot found at coordinates (" + event.lat() + ", " + event.lng() + ")"));

		spot.setOccupied(true);
		spotRepository.save(spot);
		session.setSpot(spot);

		if (session.getSector() == null) {
			Sector sector = spot.getSector();
			long activeCount = countActiveSessions(sector.getId());

			if (activeCount >= sector.getMaxCapacity()) {
				throw new SectorFullException(sector.getSectorName());
			}

			BigDecimal price = pricingService.calculateDynamicPrice(sector, activeCount);
			session.setSector(sector);
			session.setPriceApplied(price);
		}

		session.setStatus(SessionStatus.ACTIVE);
		sessionRepository.save(session);
		saveEventLog(key, "PARKED", event.licensePlate());
	}

	private void handleExit(final ExitEvent event) {

		String key = event.licensePlate() + ":EXIT:" + event.exitTime().toEpochMilli();
		if (eventLogRepository.existsByEventKey(key)) {
			return;
		}

		ParkingSession session = sessionRepository.findByLicensePlateAndStatusIn(event.licensePlate(), List.of(SessionStatus.ACTIVE)).orElseThrow(() -> new NotFoundException("No active parking session found for license plate: " + event.licensePlate()));

		BigDecimal amount = pricingService.calculateAmount(session.getPriceApplied(), session.getEntryTime(), event.exitTime());

		session.setExitTime(event.exitTime());
		session.setAmountCharged(amount);
		session.setStatus(SessionStatus.COMPLETED);

		if (session.getSpot() != null) {
			session.getSpot().setOccupied(false);
			spotRepository.save(session.getSpot());
		}

		sessionRepository.save(session);
		saveEventLog(key, "EXIT", event.licensePlate());
	}

	private long countActiveSessions(final Long sectorId) {
		return sessionRepository.countBySectorIdAndStatusIn(sectorId, List.of(SessionStatus.PENDING_SPOT, SessionStatus.ACTIVE));
	}

	private void saveEventLog(final String key, final String eventType, final String licensePlate) {
		EventLog log = new EventLog();
		log.setEventKey(key);
		log.setEventType(eventType);
		log.setLicensePlate(licensePlate);
		log.setProcessedAt(Instant.now());
		eventLogRepository.save(log);
	}
}
