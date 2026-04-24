package se.ductus.tempsensor.temperature;

import se.ductus.tempsensor.temperature.models.TemperatureSensorStateEvent;

public interface TemperatureService {
    float getCurrentTemperature();

    TemperatureSensorStateEvent readState();

    void setHeating(boolean heating);
}
