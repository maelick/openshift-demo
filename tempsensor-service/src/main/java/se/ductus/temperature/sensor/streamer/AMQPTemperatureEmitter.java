package se.ductus.temperature.sensor.streamer;

import io.quarkus.arc.lookup.LookupUnlessProperty;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ductus.temperature.sensor.temperature.models.TemperatureSensorStateEvent;

@LookupUnlessProperty(name = "se.ductus.tempsensor.temperature-stream-interval", stringValue = "off")
@ApplicationScoped
public class AMQPTemperatureEmitter implements TemperatureEmitter {
    private static final Logger log = LoggerFactory.getLogger(AMQPTemperatureEmitter.class);

    @Channel("temperature-updates")
    Emitter<TemperatureSensorStateEvent> eventEmitter;

    @ConfigProperty(name = "mp.messaging.outgoing.temperature-updates.default-routing-key")
    String rootRoutingKey;

    @Override
    public void send(TemperatureSensorStateEvent event) {
        var routingKey = rootRoutingKey + "." + event.sensorId();
        log.atInfo()
                .addKeyValue("routingKey", rootRoutingKey)
                .addKeyValue("temperatureState", event)
                .log("Sending temperature update to RabbitMQ");

        var metadata = new OutgoingRabbitMQMetadata.Builder()
                .withRoutingKey(routingKey);
        var msg = Message.of(event, Metadata.of(metadata));
        this.eventEmitter.send(msg);
    }
}
