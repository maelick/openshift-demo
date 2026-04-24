package se.ductus.thermostat.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import se.ductus.temperature.sensor.model.Heating;
import se.ductus.temperature.sensor.service.TemperatureSensorService;

@ApplicationScoped
public class HTTPHeatingService implements HeatingService {
    @Inject
    @RestClient
    TemperatureSensorService temperatureSensorService;

    @Override
    public void setHeating(String sensorId, boolean value) {
        String url = String.format("http://%s:8080", sensorId);
        temperatureSensorService.setHeating(url, new Heating(value));
    }
}
