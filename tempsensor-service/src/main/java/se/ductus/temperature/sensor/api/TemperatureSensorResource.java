package se.ductus.temperature.sensor.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import se.ductus.temperature.sensor.api.models.HeatingRequest;
import se.ductus.temperature.sensor.api.models.TemperatureResponse;
import se.ductus.temperature.sensor.temperature.TemperatureService;

@Path("/temperature-sensor")
@Produces(MediaType.APPLICATION_JSON)
public class TemperatureSensorResource {
    @Inject
    TemperatureService temperatureService;

    @GET
    @Path("/temperature")
    public TemperatureResponse getTemperature() {
        return new TemperatureResponse(temperatureService.getCurrentTemperature());
    }

    @PUT
    @Path("/heating")
    public Response setHeating(HeatingRequest request){
        if (request == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }
        this.temperatureService.setHeating(request.heating());
        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }
}
