package com.volpe.domain;

import com.volpe.dto.GarageResponse;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "sectors")
public class Sector {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String sectorName;

	@Column(nullable = false)
	private BigDecimal basePrice;

	@Column(nullable = false)
	private int maxCapacity;

	private String openHour;
	private String closeHour;
	private Integer durationLimitMinutes;

	public Sector() {
	}

	public static Sector from(final GarageResponse.SectorData data) {
		Sector sector = new Sector();
		sector.setSectorName(data.sector());
		sector.setBasePrice(data.basePrice());
		sector.setMaxCapacity(data.maxCapacity());
		sector.setOpenHour(data.openHour());
		sector.setCloseHour(data.closeHour());
		sector.setDurationLimitMinutes(data.durationLimitMinutes());
		return sector;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSectorName() {
		return sectorName;
	}

	public void setSectorName(String sectorName) {
		this.sectorName = sectorName;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public String getOpenHour() {
		return openHour;
	}

	public void setOpenHour(String openHour) {
		this.openHour = openHour;
	}

	public String getCloseHour() {
		return closeHour;
	}

	public void setCloseHour(String closeHour) {
		this.closeHour = closeHour;
	}

	public Integer getDurationLimitMinutes() {
		return durationLimitMinutes;
	}

	public void setDurationLimitMinutes(Integer durationLimitMinutes) {
		this.durationLimitMinutes = durationLimitMinutes;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Sector sector = (Sector) o;
		return Objects.equals(id, sector.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Sector.class.getSimpleName() + "[", "]").add("id=" + id).add("sectorName='" + sectorName + "'").add("basePrice=" + basePrice).add("maxCapacity=" + maxCapacity).add("openHour='" + openHour + "'").add("closeHour='" + closeHour + "'").add("durationLimitMinutes=" + durationLimitMinutes).toString();
	}
}
