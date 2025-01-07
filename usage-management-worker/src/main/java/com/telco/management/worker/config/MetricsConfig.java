package com.telco.management.worker.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class MetricsConfig {

    private final MeterRegistry registry;

    public MetricsConfig(MeterRegistry registry) {
        this.registry = registry;
    }

    @Bean
    public Timer usageUpdateTimer() {
        return Timer.builder("usage_update_time")
                .description("Time taken to update usage")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .serviceLevelObjectives(
                        Duration.ofMillis(50),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200)
                )
                .register(registry);
    }

    @Bean
    public Timer cacheUpdateTimer() {
        return Timer.builder("cache_update_time")
                .description("Time taken to update cache")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .serviceLevelObjectives(
                        Duration.ofMillis(50),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200)
                )
                .register(registry);
    }

    @Bean
    public Counter usageUpdateSuccessCounter() {
        return Counter.builder("usage_updates_success_total")
                .description("Total number of successful usage updates")
                .register(registry);
    }

    @Bean
    public Counter usageUpdateFailureCounter() {
        return Counter.builder("usage_updates_failure_total")
                .description("Total number of failed usage updates")
                .register(registry);
    }

    @Bean
    public Counter invalidUserCounter() {
        return Counter.builder("invalid_user_total")
                .description("Total number of invalid user requests")
                .register(registry);
    }

    @Bean
    public Counter invalidProductCounter() {
        return Counter.builder("invalid_product_total")
                .description("Total number of invalid product requests")
                .register(registry);
    }
}