package se.ductus.temperature.streamer;

public interface TemperatureEmitter {
    void send(TemperatureSensorStateEvent event);
}
