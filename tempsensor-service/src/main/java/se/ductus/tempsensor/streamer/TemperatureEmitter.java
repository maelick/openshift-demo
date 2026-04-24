package se.ductus.tempsensor.streamer;

import se.ductus.tempsensor.temperature.models.TemperatureSensorStateEvent;

public interface TemperatureEmitter {
    void send(TemperatureSensorStateEvent event);
}
