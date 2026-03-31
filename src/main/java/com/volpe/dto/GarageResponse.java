package com.volpe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record GarageResponse(List<SectorData> garage, List<SpotData> spots) {

    public record SectorData(
            String sector,
            @JsonProperty("base_price") BigDecimal basePrice,
            @JsonProperty("max_capacity") Integer maxCapacity,
            @JsonProperty("open_hour") String openHour,
            @JsonProperty("close_hour") String closeHour,
            @JsonProperty("duration_limit_minutes") Integer durationLimitMinutes
    ) {}

    public record SpotData(
            Long id,
            String sector,
            Double lat,
            Double lng
    ) {}
}
