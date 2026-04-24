package se.ductus.thermostat.services;

public interface HeatingService {
    void setHeating(String sensorId, boolean value);
}
