package com.volpe.dto;

import java.time.Instant;

public record ExitEvent(String licensePlate, Instant exitTime) implements ParkingEvent {
}
