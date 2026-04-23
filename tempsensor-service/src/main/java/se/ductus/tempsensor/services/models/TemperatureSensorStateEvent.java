package se.ductus.tempsensor.services.models;

import java.time.Instant;

public record TemperatureSensorStateEvent(String sensorId, float celsius, boolean heating, Instant readAt) {
}
