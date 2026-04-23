package se.ductus.tempsensor.services;

import se.ductus.tempsensor.services.models.TemperatureSensorStateEvent;

public interface TemperatureBroker {
    void send(TemperatureSensorStateEvent event);
}
