package se.ductus.tempsensor.services;

import se.ductus.tempsensor.services.models.TemperatureSensorStateEvent;

public interface TemperatureService {
    float getCurrentTemperature();

    TemperatureSensorStateEvent readState();

    void setHeating(boolean heating);
}
