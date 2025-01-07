package com.telco.management.worker.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class MetricsConfig {

    @Bean
    public Timer usageUpdateTimer(MeterRegistry registry) {
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
    public Counter usageUpdateSuccessCounter(MeterRegistry registry) {
        return Counter.builder("usage_updates_success_total")
                .description("Total number of successful usage updates")
                .register(registry);
    }

    @Bean
    public Counter usageUpdateFailureCounter(MeterRegistry registry) {
        return Counter.builder("usage_updates_failure_total")
                .description("Total number of failed usage updates")
                .register(registry);
    }

    @Bean
    public Counter deadLetterQueueCounter(MeterRegistry registry) {
        return Counter.builder("dead_letter_queue_total")
                .description("Total number of messages sent to dead letter queue")
                .register(registry);
    }

    @Bean
    public Timer cacheUpdateTimer(MeterRegistry registry) {
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
}