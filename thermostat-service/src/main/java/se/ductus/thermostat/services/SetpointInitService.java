package se.ductus.thermostat.services;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ductus.thermostat.model.TemperatureSetpoint;

import java.util.List;

@ApplicationScoped
public class SetpointInitService {
    private static final Logger log = LoggerFactory.getLogger(SetpointInitService.class);

    @Inject
    TemperatureSetpointService setpointService;

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
}
