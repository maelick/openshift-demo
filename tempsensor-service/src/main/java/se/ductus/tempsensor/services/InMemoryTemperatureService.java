package se.ductus.tempsensor.services;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApplicationScoped
public class InMemoryTemperatureService implements TemperatureService {
    public static final float DEFAULT_INCREMENT = .8f;
    public static final float DEFAULT_DECREMENT = .4f;
    public static final float MAX_TEMPERATURE = 35;
    public static final float MIN_TEMPERATURE = -40;

    private static final Logger log = LoggerFactory.getLogger(InMemoryTemperatureService.class);

    private float currentTemperature;
    private boolean heating = false;
    private final float increment;
    private final float decrement;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public InMemoryTemperatureService() {
        this(DEFAULT_INCREMENT, DEFAULT_DECREMENT);
    }

    public InMemoryTemperatureService(float initialTemperature) {
        this(initialTemperature, DEFAULT_INCREMENT, DEFAULT_DECREMENT);
    }

    public InMemoryTemperatureService(float increment, float decrement) {
        this(0, increment, decrement);
    }

    public InMemoryTemperatureService(float initialTemperature, float increment, float decrement) {
        if (increment <= 0) {
            throw new IllegalArgumentException("Temperature increment must be positive");
        }
        if (decrement <= 0) {
            throw new IllegalArgumentException("Temperature decrement must be positive");
        }

        this.currentTemperature = initialTemperature;
        this.increment = increment;
        this.decrement = decrement;
    }

    public float getCurrentTemperature() {
        var lock = this.lock.readLock();
        lock.lock();
        try {
            return currentTemperature;
        } finally {
            lock.unlock();
        }
    }

    public void setHeating(boolean heating) {
        var lock = this.lock.writeLock();
        lock.lock();
        try {
            this.heating = heating;
        } finally {
            lock.unlock();
        }
    }

    @Scheduled(every = "1s", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void updateTemperature() {
        var lock = this.lock.writeLock();
        lock.lock();
        try {
            if (this.heating) {
                this.currentTemperature = Math.min(this.currentTemperature + this.increment, MAX_TEMPERATURE);
            } else {
                this.currentTemperature = Math.max(this.currentTemperature - this.decrement, MIN_TEMPERATURE);
            }
            log.info("Temperature updated to {}°C", this.currentTemperature);
        } finally {
            lock.unlock();
        }
    }
}
