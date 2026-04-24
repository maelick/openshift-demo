package se.ductus.thermostat.services;

import io.smallrye.common.annotation.Blocking;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ductus.temperature.sensor.model.Temperature;
import se.ductus.thermostat.model.TemperatureSensorStateEvent;

@ApplicationScoped
public class RabbitMQTemperatureConsumer {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQTemperatureConsumer.class);

    @Inject
    TemperatureController temperatureController;

    @Incoming("temperature-updates")
    @Blocking
    @Transactional
    public void consume(JsonObject payload) {
        var event = payload.mapTo(TemperatureSensorStateEvent.class);
        log.atInfo()
                .addKeyValue("event", event)
                .log("Received temperature sensor event");
        temperatureController.controlTemperature(event.sensorId(), new Temperature(event.celsius()));
    }
}
