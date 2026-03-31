package com.volpe.domain;

import com.volpe.dto.GarageResponse;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "parking_spots")
public class ParkingSpot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sector_id", nullable = false)
	private Sector sector;

	@Column(nullable = false)
	private double lat;

	@Column(nullable = false)
	private double lng;

	@Column(nullable = false)
	private boolean occupied;

	public ParkingSpot() {
	}

	public static ParkingSpot from(final Sector sector, final GarageResponse.SpotData data) {
		ParkingSpot spot = new ParkingSpot();
		spot.setSector(sector);
		spot.setLat(data.lat());
		spot.setLng(data.lng());
		spot.setOccupied(false);
		return spot;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Sector getSector() {
		return sector;
	}

	public void setSector(Sector sector) {
		this.sector = sector;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ParkingSpot that = (ParkingSpot) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ParkingSpot.class.getSimpleName() + "[", "]").add("id=" + id).add("sector=" + sector).add("lat=" + lat).add("lng=" + lng).add("occupied=" + occupied).toString();
	}
}
