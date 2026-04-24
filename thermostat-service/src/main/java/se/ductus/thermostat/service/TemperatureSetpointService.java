package se.ductus.thermostat.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ductus.thermostat.model.TemperatureSetpoint;
import se.ductus.thermostat.persistence.TemperatureSetpointRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TemperatureSetpointService {
    @Inject
    TemperatureSetpointRepository temperatureSetpointRepository;

    private static final Logger log = LoggerFactory.getLogger(TemperatureSetpointService.class);

    public void updateSetpoint(TemperatureSetpoint temperatureSetpoint) {
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
