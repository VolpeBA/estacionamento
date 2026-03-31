package com.volpe.service;

import com.volpe.client.GarageSimulatorClient;
import com.volpe.domain.ParkingSpot;
import com.volpe.domain.Sector;
import com.volpe.dto.GarageResponse;
import com.volpe.repository.ParkingSpotRepository;
import com.volpe.repository.SectorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GarageInitService {

	private static final Logger log = LoggerFactory.getLogger(GarageInitService.class);

	private final GarageSimulatorClient simulatorClient;
	private final SectorRepository sectorRepository;
	private final ParkingSpotRepository spotRepository;

	public GarageInitService(final GarageSimulatorClient simulatorClient, final SectorRepository sectorRepository, final ParkingSpotRepository spotRepository) {
		this.simulatorClient = simulatorClient;
		this.sectorRepository = sectorRepository;
		this.spotRepository = spotRepository;
	}

	@Transactional
	@EventListener(ApplicationReadyEvent.class)
	public void initialize() {
		try {
			final var response = simulatorClient.fetchGarage();

			final var sectorsByName = response.garage().stream().collect(java.util.stream.Collectors.toMap(GarageResponse.SectorData::sector, sectorData -> sectorRepository.findBySectorName(sectorData.sector()).orElseGet(() -> sectorRepository.save(Sector.from(sectorData)))));

			response.spots().stream().filter(spotData -> !spotRepository.existsByLatAndLng(spotData.lat(), spotData.lng())).forEach(spotData -> {
				Sector sector = sectorsByName.get(spotData.sector());
				if (sector != null) {
					spotRepository.save(ParkingSpot.from(sector, spotData));
				}
			});

			log.info("Garage data loaded successfully");
		} catch (Exception e) {
			log.error("Failed to load garage data from simulator: {}", e.getMessage());
		}
	}
}
