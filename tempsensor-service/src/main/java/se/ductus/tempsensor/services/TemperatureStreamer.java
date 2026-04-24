package se.ductus.tempsensor.services;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class TemperatureStreamer {
    @Inject
    Instance<TemperatureBroker> brokers;

    @Inject
    TemperatureService service;

    @Scheduled(every = "${se.ductus.tempsensor.temperature-stream-interval}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void sendUpdate() {
        var event = this.service.readState();
        brokers.stream().forEach(broker -> broker.send(event));
    }
}
