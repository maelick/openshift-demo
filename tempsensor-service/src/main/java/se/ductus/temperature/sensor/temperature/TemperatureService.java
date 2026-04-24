package se.ductus.temperature.sensor.temperature;

import se.ductus.temperature.sensor.temperature.models.TemperatureSensorStateEvent;

public interface TemperatureService {
    float getCurrentTemperature();

    TemperatureSensorStateEvent readState();

    void setHeating(boolean heating);
}
