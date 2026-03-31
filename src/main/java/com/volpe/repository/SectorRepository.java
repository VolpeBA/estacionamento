package com.volpe.repository;

import com.volpe.domain.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectorRepository extends JpaRepository<Sector, Long> {

    Optional<Sector> findBySectorName(String sectorName);
}
