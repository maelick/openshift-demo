package se.ductus.temperature.sensor.service;

import io.quarkus.rest.client.reactive.Url;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.ductus.temperature.sensor.model.Temperature;

@Path("/temperature-sensor")
@RegisterRestClient(configKey = "temperature-sensor")
public interface TemperatureSensorService {
    @GET
    @Path("/temperature")
    Temperature getTemperature(@Url String url);
}
