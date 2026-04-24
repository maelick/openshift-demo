package se.ductus.temperature.streamer;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ductus.temperature.sensor.service.TemperatureSensorService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@ApplicationScoped
public class TemperatureStreamer {
    private static final Logger log = LoggerFactory.getLogger(TemperatureStreamer.class);

    @Inject
    Instance<TemperatureEmitter> brokers;

    @Inject
    @ConfigProperty(name = "se.ductus.temperature.streamer.temperature-sensors")
    List<String> temperatureSensors;

    @Inject
    @RestClient
    TemperatureSensorService temperatureSensorService;

    @Scheduled(every = "${se.ductus.temperature.streamer.temperature-stream-interval}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void sendUpdates() {
        temperatureSensors.forEach(this::sendUpdate);
    }

    private void sendUpdate(String sensorId) {
        var event = readStateEvent(sensorId);
        brokers.stream().forEach(broker -> broker.send(event));
    }

    private TemperatureSensorStateEvent readStateEvent(String sensorId) {
        log.atInfo()
                .addKeyValue("sensorId", sensorId)
                .log("Reading temperature from sensor");
        var url = String.format("http://%s:8080", sensorId);
        var temperature = temperatureSensorService.getTemperature(url);
        var now = LocalDateTime.now();
        return new TemperatureSensorStateEvent(
                sensorId,
                temperature.celsius(),
                false, // TODO need sensor to return its heating status
                now.toInstant(ZoneOffset.UTC)
        );
    }
}
