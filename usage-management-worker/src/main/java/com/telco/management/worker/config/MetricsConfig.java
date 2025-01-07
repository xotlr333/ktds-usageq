package com.telco.management.worker.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.telco.common.config.CommonMetricsConfig;

import java.time.Duration;

@Configuration
@Import(CommonMetricsConfig.class)
public class MetricsConfig {

    @Bean
    public Timer usageUpdateTimer(MeterRegistry registry) {
        return Timer.builder("usage_update_time_seconds")
                .description("Time taken to process usage update")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(5))
                .serviceLevelObjectives(
                        Duration.ofMillis(50),  // 0.05s
                        Duration.ofMillis(100), // 0.1s
                        Duration.ofMillis(200)  // 0.2s
                )
                .register(registry);
    }

    @Bean
    public Timer cacheUpdateTimer(MeterRegistry registry) {
        return Timer.builder("cache_update_time_seconds")
                .description("Time taken for cache updates")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(5))
                .serviceLevelObjectives(
                        Duration.ofMillis(50),  // 0.05s
                        Duration.ofMillis(100), // 0.1s
                        Duration.ofMillis(200)  // 0.2s
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
                .description("Total number of messages moved to Dead Letter Queue")
                .register(registry);
    }
}