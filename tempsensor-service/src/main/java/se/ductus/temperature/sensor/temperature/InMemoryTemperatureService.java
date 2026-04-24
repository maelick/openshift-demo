package se.ductus.temperature.sensor.temperature;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ductus.temperature.sensor.temperature.models.TemperatureSensorStateEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApplicationScoped
public class InMemoryTemperatureService implements TemperatureService {
    private static final Logger log = LoggerFactory.getLogger(InMemoryTemperatureService.class);

    private final String sensorId;
    private final float increment;
    private final float decrement;
    private final float minTemperature;
    private final float maxTemperature;

    private float currentTemperature;
    private boolean heating = false;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Inject
    public InMemoryTemperatureService(TemperatureServiceConfig config) {
        this(
                config.sensorId(),
                config.increment(),
                config.decrement(),
                config.minimum(),
                config.maximum()
        );

        if (config.randomInitialValue()) {
            this.currentTemperature = this.randomTemperature();
        } else {
            this.currentTemperature = config.initial();
        }
    }

    public InMemoryTemperatureService(Optional<String> sensorId, float increment, float decrement, float minTemperature, float maxTemperature, float initialValue) {
        this(sensorId, increment, decrement, minTemperature, maxTemperature);
        this.currentTemperature = initialValue;
    }

    public InMemoryTemperatureService(Optional<String> sensorId, float increment, float decrement, float minTemperature, float maxTemperature) {
        if (minTemperature >= maxTemperature) {
            throw new IllegalArgumentException("Minimum temperature must be less than maximum");
        }

        this.sensorId = sensorId.orElse(randomSensorId());
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
                    this.sensorId,
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
                    .addKeyValue("sensorId", this.sensorId)
                    .addKeyValue("celsius", this.currentTemperature)
                    .log("Temperature updated");
        } finally {
            lock.unlock();
        }
    }

    private float randomTemperature() {
        return this.minTemperature + (float) (Math.random() * (this.maxTemperature - this.minTemperature));
    }

    private static String randomSensorId() {
        return "temperature-sensor-" + UUID.randomUUID();
    }
}
