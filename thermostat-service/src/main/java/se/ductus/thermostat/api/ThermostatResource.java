package se.ductus.thermostat.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import se.ductus.thermostat.api.dto.TemperatureSetpointDto;
import se.ductus.thermostat.model.TemperatureSetpoint;
import se.ductus.thermostat.service.ThermostatService;

import java.util.ArrayList;
import java.util.List;

@Path("/thermostat")
@Produces(MediaType.APPLICATION_JSON)
public class ThermostatResource {

    @Inject
    ThermostatService thermostatService;

    @PUT
    @Path("/setpoint")
    public Response setThermostatTemperature(TemperatureSetpointDto temperatureSetpointDto) {
        thermostatService.updateSetpoint(
                new se.ductus.thermostat.model.TemperatureSetpoint(
                        temperatureSetpointDto.temperatureSensorId,
                        temperatureSetpointDto.celsius
                )
        );
        return Response
                .status(Response.Status.OK)
                .build();

    }

    @GET
    @Path("/setpoint")
    public List<TemperatureSetpointDto> getThermostatTemperature() {
        List<TemperatureSetpoint> temperatureSetpoints = thermostatService.getSetpoints();
        List<TemperatureSetpointDto> temperatureSetpointDtos = new ArrayList<>();
        for (TemperatureSetpoint temperatureSetpoint : temperatureSetpoints) {
            temperatureSetpointDtos.add(
                    new TemperatureSetpointDto(
                            temperatureSetpoint.temperatureSensorId(),
                            temperatureSetpoint.celsius()
                    )
            );
        }
        return temperatureSetpointDtos;
    }

    @GET
    @Path("/setpoint/{temperatureSensorId}")
    public TemperatureSetpointDto getThermostatTemperature(@PathParam("temperatureSensorId") String temperatureSensorId) {
        var temperatureSetpoint = thermostatService.getSetpoint(temperatureSensorId);
        if (temperatureSetpoint.isEmpty()) {
            throw new NotFoundException(String.format(
                    "temperature setpoint not found for the given temperature sensor: %s", temperatureSensorId
            ));
        }
        return new TemperatureSetpointDto(
                temperatureSetpoint.get().temperatureSensorId(),
                temperatureSetpoint.get().celsius()
        );
    }
}
