package se.ductus.tempsensor.services;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TemperatureStreamer {
    @Inject
    TemperatureBroker broker;

    @Inject
    TemperatureService service;

    @Scheduled(every = "${se.ductus.tempsensor.stream-interval:1s}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void sendUpdate() {
        var event = this.service.readState();
        broker.send(event);
    }
}
