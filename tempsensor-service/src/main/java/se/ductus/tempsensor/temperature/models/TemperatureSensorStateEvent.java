package se.ductus.tempsensor.temperature.models;

import java.time.Instant;

public record TemperatureSensorStateEvent(String sensorId, float celsius, boolean heating, Instant readAt) {
}
