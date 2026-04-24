package se.ductus.tempsensor.services;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import jakarta.validation.constraints.Positive;

import java.util.Optional;

@ConfigMapping(prefix = "se.ductus.tempsensor.temperature")
public interface TemperatureServiceConfig {
    Optional<String> sensorId();

    @WithDefault("0.8")
    @Positive()
    float increment();

    @WithDefault("0.4")
    @Positive()
    float decrement();

    @WithDefault("-40")
    float minimum();

    @WithDefault("35")
    float maximum();

    @WithDefault("0")
    float initial();

    @WithDefault("false")
    boolean randomInitialValue();
}
