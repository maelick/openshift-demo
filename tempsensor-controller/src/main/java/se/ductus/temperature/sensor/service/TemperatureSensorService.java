package se.ductus.temperature.sensor.service;

import io.quarkus.rest.client.reactive.Url;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.ductus.temperature.sensor.model.Heating;

@Path("/temperature-sensor")
@RegisterRestClient(configKey = "temperature-sensor")
public interface TemperatureSensorService {
    @PUT
    @Path("/heating")
    void setHeating(@Url String url, Heating heating);
}
