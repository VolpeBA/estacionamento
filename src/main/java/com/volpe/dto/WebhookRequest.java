package com.volpe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record WebhookRequest(
        @NotNull @JsonProperty("license_plate") String licensePlate,
        @NotNull @JsonProperty("event_type") String eventType,
        @JsonProperty("entry_time") Instant entryTime,
        @JsonProperty("exit_time") Instant exitTime,
        Double lat,
        Double lng,
        String sector
) {
}
