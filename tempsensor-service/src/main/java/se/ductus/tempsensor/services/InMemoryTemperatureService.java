package se.ductus.tempsensor.services;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InMemoryTemperatureService implements TemperatureService {
    public float getCurrentTemperature() {
        return 0;
    }

    public void setHeating(boolean heating) {
    }
}
