package se.ductus.tempsensor.services;

public interface TemperatureService {
    float getCurrentTemperature();

    void setHeating(boolean heating);
}
