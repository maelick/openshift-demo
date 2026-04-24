package se.ductus.thermostat.services;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ductus.temperaturesensor.service.TemperatureSensorService;
import se.ductus.thermostat.model.TemperatureSetpoint;

import java.util.List;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class PollingThermostatService {
    private static final Logger log = LoggerFactory.getLogger(PollingThermostatService.class);

    @Inject
    TemperatureSetpointService setpointService;

    @Inject
    @RestClient
    TemperatureSensorService temperatureSensorService;

    @Inject
    TemperatureController temperatureController;

    @Inject
    @ConfigProperty(name = "se.ductus.thermostat.temperature-sensors")
    List<String> temperatureSensors;

    void onStart(@Observes StartupEvent ev) {
        for (String temperatureSensor : temperatureSensors) {
            if (setpointService.getSetpoint(temperatureSensor).isEmpty()) {
                this.setpointService.updateSetpoint(new TemperatureSetpoint(temperatureSensor, 0));
            }
        }
    }

    @Scheduled(every = "1s")
    synchronized void controlTemperature() {
        this.temperatureSensors.forEach(this::controlSensorTemperature);
    }

    private void controlSensorTemperature(String sensorId) {
        String url = String.format("http://%s:8080", sensorId);
        var temperature = temperatureSensorService.getTemperature(url);
        temperatureController.controlTemperature(sensorId, temperature);
    }
}
