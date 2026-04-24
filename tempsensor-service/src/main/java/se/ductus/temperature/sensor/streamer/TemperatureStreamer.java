package se.ductus.temperature.sensor.streamer;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import se.ductus.temperature.sensor.temperature.TemperatureService;

@ApplicationScoped
public class TemperatureStreamer {
    @Inject
    Instance<TemperatureEmitter> brokers;

    @Inject
    TemperatureService service;

    @Scheduled(every = "${se.ductus.tempsensor.temperature-stream-interval}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void sendUpdate() {
        var event = this.service.readState();
        brokers.stream().forEach(broker -> broker.send(event));
    }
}
