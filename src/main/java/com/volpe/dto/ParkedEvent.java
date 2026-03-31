package com.volpe.dto;

public record ParkedEvent(String licensePlate, double lat, double lng) implements ParkingEvent {
}
