package com.volpe.domain;

import com.volpe.dto.EntryEvent;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "parking_sessions")
public class ParkingSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String licensePlate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sector_id")
	private Sector sector;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "spot_id")
	private ParkingSpot spot;

	@Column(nullable = false)
	private Instant entryTime;

	private Instant exitTime;

	private BigDecimal priceApplied;

	private BigDecimal amountCharged;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SessionStatus status;

	public ParkingSession() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public Sector getSector() {
		return sector;
	}

	public void setSector(Sector sector) {
		this.sector = sector;
	}

	public ParkingSpot getSpot() {
		return spot;
	}

	public void setSpot(ParkingSpot spot) {
		this.spot = spot;
	}

	public Instant getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(Instant entryTime) {
		this.entryTime = entryTime;
	}

	public Instant getExitTime() {
		return exitTime;
	}

	public void setExitTime(Instant exitTime) {
		this.exitTime = exitTime;
	}

	public BigDecimal getPriceApplied() {
		return priceApplied;
	}

	public void setPriceApplied(BigDecimal priceApplied) {
		this.priceApplied = priceApplied;
	}

	public BigDecimal getAmountCharged() {
		return amountCharged;
	}

	public void setAmountCharged(BigDecimal amountCharged) {
		this.amountCharged = amountCharged;
	}

	public SessionStatus getStatus() {
		return status;
	}

	public void setStatus(SessionStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ParkingSession that = (ParkingSession) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ParkingSession.class.getSimpleName() + "[", "]").add("id=" + id).add("licensePlate='" + licensePlate + "'").add("sector=" + sector).add("spot=" + spot).add("entryTime=" + entryTime).add("exitTime=" + exitTime).add("priceApplied=" + priceApplied).add("amountCharged=" + amountCharged).add("status=" + status).toString();
	}

	public static ParkingSession from(final @NotNull EntryEvent event) {

		ParkingSession session = new ParkingSession();

		session.setLicensePlate(event.licensePlate());
		session.setEntryTime(event.entryTime());
		session.setStatus(SessionStatus.PENDING_SPOT);

		return session;
	}
}
