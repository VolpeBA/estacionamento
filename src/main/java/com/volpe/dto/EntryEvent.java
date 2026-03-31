package com.volpe.dto;

import java.time.Instant;

public record EntryEvent(String licensePlate, Instant entryTime, String sector) implements ParkingEvent {
}
