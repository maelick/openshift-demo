package se.ductus.temperature.sensor.streamer;

import se.ductus.temperature.sensor.temperature.models.TemperatureSensorStateEvent;

public interface TemperatureEmitter {
    void send(TemperatureSensorStateEvent event);
}
