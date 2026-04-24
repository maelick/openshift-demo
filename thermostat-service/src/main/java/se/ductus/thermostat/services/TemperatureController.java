package se.ductus.thermostat.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ductus.temperature.sensor.model.Temperature;

@ApplicationScoped
public class TemperatureController {
    private static final Logger log = LoggerFactory.getLogger(TemperatureController.class);

    @Inject
    TemperatureSetpointService setpointService;

    @Inject
    HeatingService heatingService;

    public void controlTemperature(String sensorId, Temperature temperature) {
        var temperatureSetpoint = setpointService.getSetpoint(sensorId);
        if (temperatureSetpoint.isEmpty()) {
            // We skip sensors that don't have an existing setpoint
            log.atInfo()
                    .addKeyValue("sensorId", sensorId)
                    .log("Skipping sensor without setpoint");
            return;
        }

        var isTooCold = temperature.celsius() < temperatureSetpoint.get().celsius();
        log.atInfo()
                .addKeyValue("setpoint", temperatureSetpoint.get())
                .addKeyValue("heating", isTooCold)
                .addKeyValue("temperature", temperature)
                .log("Setting sensor heating");
        heatingService.setHeating(sensorId, isTooCold);
    }
}
