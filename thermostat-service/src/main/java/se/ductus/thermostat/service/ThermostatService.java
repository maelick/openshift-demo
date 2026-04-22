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
import se.ductus.temperaturesensor.model.Temperature;
import se.ductus.temperaturesensor.service.TemperatureSensorService;
import se.ductus.thermostat.model.TemperatureSetpoint;
import se.ductus.thermostat.persistence.TemperatureSetpointRepository;

import java.util.ArrayList;
import java.util.List;
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
            try {
                this.getSetpoint(temperatureSensor);
            }
            catch (NotFoundException ignored) {
                this.updateSetpoint(new TemperatureSetpoint(temperatureSensor, 0));
            }
        }
    }

    @Scheduled(every = "1s")
    synchronized void controlTemperature() {
        log.info("Getting setpoints");

        List<TemperatureSetpoint> temperatureSetpoints = new ArrayList<>();
        for (String temperatureSensor : temperatureSensors) {
            try {
                TemperatureSetpoint temperatureSetpoint = this.getSetpoint(temperatureSensor);
                temperatureSetpoints.add(temperatureSetpoint);
            }
            catch (NotFoundException ignored) {
                log.warn("setpoint not found for sensor {}", temperatureSensor);
            }
        }

        log.info("Checking sensor temperatures");
        for (TemperatureSetpoint temperatureSetpoint : temperatureSetpoints) {
            String url = String.format("http://%s:8080", temperatureSetpoint.temperatureSensorId);
            Temperature temperature = temperatureSensorService.getTemperature(url);
            if (temperature.celsius < temperatureSetpoint.celsius) {
                log.info("Setting sensor {} heating=true (temperature={}, setpoint={})",
                        temperatureSetpoint.temperatureSensorId,
                        temperature.celsius,
                        temperatureSetpoint.celsius
                );
                temperatureSensorService.setHeating(url, new Heating(true));
            }
            if (temperatureSetpoint.celsius > temperature.celsius) {
                log.info("Setting sensor {} heating=false (temperature={}, setpoint={})",
                        temperatureSetpoint.temperatureSensorId,
                        temperature.celsius,
                        temperatureSetpoint.celsius
                );
                temperatureSensorService.setHeating(url, new Heating(false));
            }

        }
    }

    public void updateSetpoint(TemperatureSetpoint temperatureSetpoint) {
        if (!temperatureSensors.contains(temperatureSetpoint.temperatureSensorId)) {
            throw new NotFoundException();
        }
        temperatureSetpointRepository.updateTemperatureSetpoint(temperatureSetpoint);
    }

    public TemperatureSetpoint getSetpoint(String temperatureSensorId) throws NotFoundException {
        return temperatureSetpointRepository.getTemperatureSetpoint(temperatureSensorId);
    }

    public List<TemperatureSetpoint> getSetpoints() {
        return temperatureSetpointRepository.getTemperatureSetpoints();
    }
}
