package se.ductus.tempsensor.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import se.ductus.tempsensor.api.models.HeatingRequest;
import se.ductus.tempsensor.api.models.TemperatureResponse;

@Path("/temperature-sensor")
@Produces(MediaType.APPLICATION_JSON)
public class TemperatureSensorResource {

    @GET
    @Path("/temperature")
    public TemperatureResponse getTemperature() {
        return new TemperatureResponse(0);
    }

    @PUT
    @Path("/heating")
    public Response setHeating(HeatingRequest request){
        if (request == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }
        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }
}
