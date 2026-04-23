package se.ductus.tempsensor.services;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ductus.tempsensor.services.models.TemperatureSensorStateEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApplicationScoped
public class InMemoryTemperatureService implements TemperatureService {
    private static final Logger log = LoggerFactory.getLogger(InMemoryTemperatureService.class);

    private final float increment;
    private final float decrement;
    private final float minTemperature;
    private final float maxTemperature;

    private float currentTemperature;
    private boolean heating = false;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Inject
    public InMemoryTemperatureService(TemperatureServiceConfig config) {
        this(config.increment(), config.decrement(), config.minimum(), config.maximum());

        if (config.randomInitialValue()) {
            this.currentTemperature = this.randomTemperature();
        } else {
            this.currentTemperature = config.initial();
        }
    }

    public InMemoryTemperatureService(float increment, float decrement, float minTemperature, float maxTemperature, float initialValue) {
        this(increment, decrement, minTemperature, maxTemperature);
        this.currentTemperature = initialValue;
    }

    public InMemoryTemperatureService(float increment, float decrement, float minTemperature, float maxTemperature) {
        if (minTemperature >= maxTemperature) {
            throw new IllegalArgumentException("Minimum temperature must be less than maximum");
        }

        this.increment = increment;
        this.decrement = decrement;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
    }

    @Override
    public float getCurrentTemperature() {
        var lock = this.lock.readLock();
        lock.lock();
        try {
            return currentTemperature;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public TemperatureSensorStateEvent readState() {
        var lock = this.lock.readLock();
        lock.lock();
        try {
            var now = LocalDateTime.now();
            return new TemperatureSensorStateEvent(
                    "id-todo",
                    this.currentTemperature,
                    this.heating,
                    now.toInstant(ZoneOffset.UTC)
            );
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setHeating(boolean heating) {
        var lock = this.lock.writeLock();
        lock.lock();
        try {
            this.heating = heating;
        } finally {
            lock.unlock();
        }
    }

    @Scheduled(every = "${se.ductus.tempsensor.temperature-update-interval}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void updateTemperature() {
        var lock = this.lock.writeLock();
        lock.lock();
        try {
            if (this.heating) {
                this.currentTemperature = Math.min(this.currentTemperature + this.increment, this.maxTemperature);
            } else {
                this.currentTemperature = Math.max(this.currentTemperature - this.decrement, this.minTemperature);
            }
            log.atInfo()
                    .addKeyValue("celsius", this.currentTemperature)
                    .log("Temperature updated");
        } finally {
            lock.unlock();
        }
    }

    private float randomTemperature() {
        return this.minTemperature + (float) (Math.random() * (this.maxTemperature - this.minTemperature));
    }
}
