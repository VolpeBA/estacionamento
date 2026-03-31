package com.volpe.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "event_logs")
public class EventLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String eventKey;

	@Column(nullable = false)
	private String eventType;

	@Column(nullable = false)
	private String licensePlate;

	@Column(nullable = false)
	private Instant processedAt;

	public EventLog() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public Instant getProcessedAt() {
		return processedAt;
	}

	public void setProcessedAt(Instant processedAt) {
		this.processedAt = processedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		EventLog eventLog = (EventLog) o;
		return Objects.equals(id, eventLog.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", EventLog.class.getSimpleName() + "[", "]").add("id=" + id).add("eventKey='" + eventKey + "'").add("eventType='" + eventType + "'").add("licensePlate='" + licensePlate + "'").add("processedAt=" + processedAt).toString();
	}
}