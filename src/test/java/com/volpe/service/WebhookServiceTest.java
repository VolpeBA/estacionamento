package com.volpe.service;

import com.volpe.domain.ParkingSession;
import com.volpe.domain.ParkingSpot;
import com.volpe.domain.Sector;
import com.volpe.domain.SessionStatus;
import com.volpe.dto.WebhookRequest;
import com.volpe.exception.NotFoundException;
import com.volpe.exception.SectorFullException;
import com.volpe.repository.EventLogRepository;
import com.volpe.repository.ParkingSessionRepository;
import com.volpe.repository.ParkingSpotRepository;
import com.volpe.repository.SectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

	@Mock
	private SectorRepository sectorRepository;

	@Mock
	private ParkingSpotRepository spotRepository;

	@Mock
	private ParkingSessionRepository sessionRepository;

	@Mock
	private EventLogRepository eventLogRepository;

	@Mock
	private PricingService pricingService;

	@InjectMocks
	private WebhookService webhookService;

	private Sector sector;

	@BeforeEach
	void setUp() {
		sector = new Sector();
		sector.setId(1L);
		sector.setSectorName("A");
		sector.setBasePrice(new BigDecimal("10.00"));
		sector.setMaxCapacity(10);
	}

	@Test
	void shouldCreateSessionOnEntryWithSector() {
		WebhookRequest request = new WebhookRequest("ZUL0001", "ENTRY", Instant.now(), null, null, null, "A");

		when(eventLogRepository.existsByEventKey(any())).thenReturn(false);
		when(sectorRepository.findBySectorName("A")).thenReturn(Optional.of(sector));
		when(sessionRepository.countBySectorIdAndStatusIn(eq(1L), anyList())).thenReturn(5L);
		when(pricingService.calculateDynamicPrice(eq(sector), eq(5L))).thenReturn(new BigDecimal("10.00"));
		when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(eventLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		webhookService.process(request);

		verify(sessionRepository).save(any(ParkingSession.class));
	}

	@Test
	void shouldRejectEntryWhenSectorIsFull() {
		WebhookRequest request = new WebhookRequest("ZUL0001", "ENTRY", Instant.now(), null, null, null, "A");

		when(eventLogRepository.existsByEventKey(any())).thenReturn(false);
		when(sectorRepository.findBySectorName("A")).thenReturn(Optional.of(sector));
		when(sessionRepository.countBySectorIdAndStatusIn(eq(1L), anyList())).thenReturn(10L);

		assertThrows(SectorFullException.class, () -> webhookService.process(request));

		verify(sessionRepository, never()).save(any());
	}

	@Test
	void shouldCreatePendingSessionOnEntryWithoutSector() {
		WebhookRequest request = new WebhookRequest("ZUL0001", "ENTRY", Instant.now(), null, null, null, null);

		when(eventLogRepository.existsByEventKey(any())).thenReturn(false);
		when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(eventLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		webhookService.process(request);

		verify(sessionRepository).save(any(ParkingSession.class));
		verify(sectorRepository, never()).findBySectorName(any());
	}

	@Test
	void shouldIgnoreDuplicateEntryEvent() {
		WebhookRequest request = new WebhookRequest("ZUL0001", "ENTRY", Instant.now(), null, null, null, "A");

		when(eventLogRepository.existsByEventKey(any())).thenReturn(true);

		webhookService.process(request);

		verify(sessionRepository, never()).save(any());
	}

	@Test
	void shouldAssociateSpotToSessionOnParked() {
		WebhookRequest request = new WebhookRequest("ZUL0001", "PARKED", null, null, -23.561684, -46.655981, null);

		ParkingSession session = new ParkingSession();
		session.setLicensePlate("ZUL0001");
		session.setEntryTime(Instant.now());
		session.setStatus(SessionStatus.ACTIVE);
		session.setSector(sector);
		session.setPriceApplied(new BigDecimal("10.00"));

		ParkingSpot spot = new ParkingSpot();
		spot.setSector(sector);
		spot.setOccupied(false);

		when(eventLogRepository.existsByEventKey(any())).thenReturn(false);
		when(sessionRepository.findByLicensePlateAndStatusIn(eq("ZUL0001"), anyList())).thenReturn(Optional.of(session));
		when(spotRepository.findByLatAndLng(-23.561684, -46.655981)).thenReturn(Optional.of(spot));
		when(spotRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(eventLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		webhookService.process(request);

		verify(sessionRepository).save(any(ParkingSession.class));
		verify(spotRepository).save(spot);
	}

	@Test
	void shouldCalculatePriceOnParkedWhenEntryHadNoSector() {
		WebhookRequest request = new WebhookRequest("ZUL0001", "PARKED", null, null, -23.561684, -46.655981, null);

		ParkingSession session = new ParkingSession();
		session.setLicensePlate("ZUL0001");
		session.setEntryTime(Instant.now());
		session.setStatus(SessionStatus.PENDING_SPOT);

		ParkingSpot spot = new ParkingSpot();
		spot.setSector(sector);
		spot.setOccupied(false);

		when(eventLogRepository.existsByEventKey(any())).thenReturn(false);
		when(sessionRepository.findByLicensePlateAndStatusIn(eq("ZUL0001"), anyList())).thenReturn(Optional.of(session));
		when(spotRepository.findByLatAndLng(-23.561684, -46.655981)).thenReturn(Optional.of(spot));
		when(sessionRepository.countBySectorIdAndStatusIn(eq(1L), anyList())).thenReturn(3L);
		when(pricingService.calculateDynamicPrice(eq(sector), eq(3L))).thenReturn(new BigDecimal("10.00"));
		when(spotRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(eventLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		webhookService.process(request);

		verify(pricingService).calculateDynamicPrice(eq(sector), eq(3L));
		verify(sessionRepository).save(any(ParkingSession.class));
	}

	@Test
	void shouldThrowWhenSpotNotFoundOnParked() {
		WebhookRequest request = new WebhookRequest("ZUL0001", "PARKED", null, null, -99.0, -99.0, null);

		ParkingSession session = new ParkingSession();
		session.setLicensePlate("ZUL0001");
		session.setStatus(SessionStatus.ACTIVE);
		session.setSector(sector);

		when(eventLogRepository.existsByEventKey(any())).thenReturn(false);
		when(sessionRepository.findByLicensePlateAndStatusIn(eq("ZUL0001"), anyList())).thenReturn(Optional.of(session));
		when(spotRepository.findByLatAndLng(-99.0, -99.0)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> webhookService.process(request));
		verify(sessionRepository, never()).save(any());
	}

	@Test
	void shouldIgnoreDuplicateParkedEvent() {
		WebhookRequest request = new WebhookRequest("ZUL0001", "PARKED", null, null, -23.561684, -46.655981, null);

		when(eventLogRepository.existsByEventKey(any())).thenReturn(true);

		webhookService.process(request);

		verify(sessionRepository, never()).save(any());
	}

	@Test
	void shouldThrowExceptionOnExitWhenNoActiveSession() {
		WebhookRequest request = new WebhookRequest("ZUL9999", "EXIT", null, Instant.now(), null, null, null);

		when(eventLogRepository.existsByEventKey(any())).thenReturn(false);
		when(sessionRepository.findByLicensePlateAndStatusIn(eq("ZUL9999"), anyList())).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> webhookService.process(request));
	}

	@Test
	void shouldCompleteSessionOnExit() {
		Instant entryTime = Instant.now().minusSeconds(7200);
		Instant exitTime = Instant.now();

		WebhookRequest request = new WebhookRequest("ZUL0001", "EXIT", null, exitTime, null, null, null);

		ParkingSession session = new ParkingSession();
		session.setLicensePlate("ZUL0001");
		session.setEntryTime(entryTime);
		session.setStatus(SessionStatus.ACTIVE);
		session.setPriceApplied(new BigDecimal("10.00"));

		when(eventLogRepository.existsByEventKey(any())).thenReturn(false);
		when(sessionRepository.findByLicensePlateAndStatusIn(eq("ZUL0001"), anyList())).thenReturn(Optional.of(session));
		when(pricingService.calculateAmount(any(), any(), any())).thenReturn(new BigDecimal("20.00"));
		when(sessionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(eventLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		webhookService.process(request);

		verify(sessionRepository).save(any(ParkingSession.class));
	}
}
