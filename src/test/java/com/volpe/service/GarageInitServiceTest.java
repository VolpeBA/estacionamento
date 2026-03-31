package com.volpe.service;

import com.volpe.client.GarageSimulatorClient;
import com.volpe.domain.Sector;
import com.volpe.dto.GarageResponse;
import com.volpe.repository.ParkingSpotRepository;
import com.volpe.repository.SectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarageInitServiceTest {

    @Mock
    private GarageSimulatorClient simulatorClient;

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private ParkingSpotRepository spotRepository;

    @InjectMocks
    private GarageInitService garageInitService;

    private GarageResponse.SpotData spotData;
    private GarageResponse.SectorData sectorData;
    private GarageResponse garageResponse;

    @BeforeEach
    void setUp() {
        spotData = new GarageResponse.SpotData(1L, "A", -23.561684, -46.655981);
        sectorData = new GarageResponse.SectorData("A", new BigDecimal("10.00"), 100, "08:00", "22:00", 1440);
        garageResponse = new GarageResponse(List.of(sectorData), List.of(spotData));
    }

    @Test
    void shouldSaveNewSectorAndSpotOnStartup() {
        when(simulatorClient.fetchGarage()).thenReturn(garageResponse);
        when(sectorRepository.findBySectorName("A")).thenReturn(Optional.empty());
        when(sectorRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(spotRepository.existsByLatAndLng(-23.561684, -46.655981)).thenReturn(false);
        when(spotRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        garageInitService.initialize();

        verify(sectorRepository).save(any(Sector.class));
        verify(spotRepository).save(any());
    }

    @Test
    void shouldSkipExistingSectorOnStartup() {
        Sector existingSector = new Sector();
        existingSector.setSectorName("A");

        when(simulatorClient.fetchGarage()).thenReturn(garageResponse);
        when(sectorRepository.findBySectorName("A")).thenReturn(Optional.of(existingSector));
        when(spotRepository.existsByLatAndLng(-23.561684, -46.655981)).thenReturn(false);
        when(spotRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        garageInitService.initialize();

        verify(sectorRepository, never()).save(any());
        verify(spotRepository).save(any());
    }

    @Test
    void shouldSkipExistingSpotOnStartup() {
        Sector existingSector = new Sector();
        existingSector.setSectorName("A");

        when(simulatorClient.fetchGarage()).thenReturn(garageResponse);
        when(sectorRepository.findBySectorName("A")).thenReturn(Optional.of(existingSector));
        when(spotRepository.existsByLatAndLng(-23.561684, -46.655981)).thenReturn(true);

        garageInitService.initialize();

        verify(spotRepository, never()).save(any());
    }

    @Test
    void shouldHandleMultipleSectorsAndSpots() {
        GarageResponse.SpotData spot2 = new GarageResponse.SpotData(2L, "B", -23.562000, -46.656000);
        GarageResponse.SectorData sectorB = new GarageResponse.SectorData("B", new BigDecimal("15.00"), 50, "06:00", "23:00", 720);
        GarageResponse multiSectorResponse = new GarageResponse(List.of(sectorData, sectorB), List.of(spotData, spot2));

        when(simulatorClient.fetchGarage()).thenReturn(multiSectorResponse);
        when(sectorRepository.findBySectorName(any())).thenReturn(Optional.empty());
        when(sectorRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(spotRepository.existsByLatAndLng(anyDouble(), anyDouble())).thenReturn(false);
        when(spotRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        garageInitService.initialize();

        verify(sectorRepository, times(2)).save(any(Sector.class));
        verify(spotRepository, times(2)).save(any());
    }
}
