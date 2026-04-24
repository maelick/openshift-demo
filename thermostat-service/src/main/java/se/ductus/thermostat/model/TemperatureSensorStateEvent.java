package se.ductus.thermostat.model;

import java.time.Instant;

public record TemperatureSensorStateEvent(String sensorId, float celsius, boolean heating, Instant readAt) {
}
