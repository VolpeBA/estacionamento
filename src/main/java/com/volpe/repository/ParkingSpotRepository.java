package com.volpe.repository;

import com.volpe.domain.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    Optional<ParkingSpot> findByLatAndLng(double lat, double lng);

    boolean existsByLatAndLng(double lat, double lng);
}
