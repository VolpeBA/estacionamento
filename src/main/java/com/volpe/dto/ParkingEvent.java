package com.volpe.dto;

public sealed interface ParkingEvent permits EntryEvent, ParkedEvent, ExitEvent {

	static ParkingEvent from(final WebhookRequest req) {
		return switch (req.eventType()) {
			case "ENTRY" -> new EntryEvent(req.licensePlate(), req.entryTime(), req.sector());
			case "PARKED" -> new ParkedEvent(req.licensePlate(), req.lat(), req.lng());
			case "EXIT" -> new ExitEvent(req.licensePlate(), req.exitTime());
			default -> throw new IllegalArgumentException("Unknown event type: " + req.eventType());
		};
	}
}
