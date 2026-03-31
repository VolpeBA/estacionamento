package com.volpe.repository;

import com.volpe.domain.ParkingSession;
import com.volpe.domain.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

	Optional<ParkingSession> findByLicensePlateAndStatusIn(String licensePlate, List<SessionStatus> statuses);

	long countBySectorIdAndStatusIn(Long sectorId, List<SessionStatus> statuses);

	@Query("""
			select COALESCE(SUM(ps.amountCharged), 0)
			from ParkingSession ps
			where ps.sector.sectorName = :sector
			and ps.exitTime >= :startOfDay
			and ps.exitTime < :startOfNextDay
			and ps.status = com.volpe.domain.SessionStatus.COMPLETED
			""")
	BigDecimal sumAmountBySectorAndDate(@Param("sector") String sector, @Param("startOfDay") Instant startOfDay, @Param("startOfNextDay") Instant startOfNextDay);
}
