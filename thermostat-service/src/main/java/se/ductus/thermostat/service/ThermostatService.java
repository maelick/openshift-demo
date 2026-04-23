package se.ductus.thermostat.service;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ductus.temperaturesensor.model.Heating;
import se.ductus.temperaturesensor.service.TemperatureSensorService;
import se.ductus.thermostat.model.TemperatureSetpoint;
import se.ductus.thermostat.persistence.TemperatureSetpointRepository;

import java.util.List;
import java.util.Optional;

import io.quarkus.scheduler.Scheduled;

@Singleton
public class ThermostatService {
    private static final Logger log = LoggerFactory.getLogger(ThermostatService.class);

    @Inject
    TemperatureSetpointRepository temperatureSetpointRepository;

    @Inject
    @RestClient
    TemperatureSensorService temperatureSensorService;

    @Inject
    @ConfigProperty(name = "se.ductus.thermostat.temperature-sensors")
    List<String> temperatureSensors;

    void onStart(@Observes StartupEvent ev) {
        for (String temperatureSensor : temperatureSensors) {
            if (this.getSetpoint(temperatureSensor).isEmpty()) {
                this.updateSetpoint(new TemperatureSetpoint(temperatureSensor, 0));
            }
        }
    }

    @Scheduled(every = "1s")
    synchronized void controlTemperature() {
        this.temperatureSensors.forEach(this::controlSensorTemperature);
    }

    private void controlSensorTemperature(String sensorId) {
        var temperatureSetpoint = this.getSetpoint(sensorId);
        if (temperatureSetpoint.isEmpty()) {
            // This should normally never happen as long as there is a single thermostat service running
            log.atWarn()
                    .addKeyValue("sensorId", sensorId)
                    .log("Setpoint not found for sensor {}", sensorId);
            return;
        }

        String url = String.format("http://%s:8080", temperatureSetpoint.get().temperatureSensorId());
        var temperature = temperatureSensorService.getTemperature(url);
        var isTooCold = temperature.celsius() < temperatureSetpoint.get().celsius();
        log.atInfo()
                .addKeyValue("setpoint", temperatureSetpoint.get())
                .addKeyValue("heating", isTooCold)
                .addKeyValue("temperature", temperature)
                .log("Setting sensor heating");
        temperatureSensorService.setHeating(url, new Heating(isTooCold));
    }

    public void updateSetpoint(TemperatureSetpoint temperatureSetpoint) {
        if (!temperatureSensors.contains(temperatureSetpoint.temperatureSensorId())) {
            throw new NotFoundException();
        }
        temperatureSetpointRepository.updateTemperatureSetpoint(temperatureSetpoint);
    }

    public Optional<TemperatureSetpoint> getSetpoint(String temperatureSensorId) {
        log.atDebug()
                .addKeyValue("sensorId", temperatureSensorId)
                .log("Getting temperature setpoint");
        return temperatureSetpointRepository.getTemperatureSetpoint(temperatureSensorId);
    }

    public List<TemperatureSetpoint> getSetpoints() {
        return temperatureSetpointRepository.getTemperatureSetpoints();
    }
}
