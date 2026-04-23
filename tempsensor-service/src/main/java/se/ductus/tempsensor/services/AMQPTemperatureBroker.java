package se.ductus.tempsensor.services;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import se.ductus.tempsensor.services.models.TemperatureSensorStateEvent;

public class AMQPTemperatureBroker implements TemperatureBroker {
    @Channel("temperature-updates")
    Emitter<TemperatureSensorStateEvent> eventEmitter;

    @Override
    public void send(TemperatureSensorStateEvent event) {
        var metadata = new OutgoingRabbitMQMetadata.Builder()
                .withRoutingKey("routing-key-TODO");
        var msg = Message.of(event, Metadata.of(metadata));
        this.eventEmitter.send(msg);
    }
}
