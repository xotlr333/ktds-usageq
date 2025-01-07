package com.telco.common.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CommonMetricsConfig {

    // Cache related metrics
    @Bean
    public Timer cacheOperationTimer(MeterRegistry registry) {
        return Timer.builder("cache_operation_time_seconds")
                .description("Time taken for cache operations")
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
    public Counter usageInvalidErrorCounter(MeterRegistry registry) {
        return Counter.builder("usage_update_errors_total")
                .description("Total number of usage update errors")
                .register(registry);
    }

    @Bean
    public Counter cacheHitCounter(MeterRegistry registry) {
        return Counter.builder("cache_hits_total")
                .description("Total number of cache hits")
                .register(registry);
    }

    @Bean
    public Counter cacheMissCounter(MeterRegistry registry) {
        return Counter.builder("cache_misses_total")
                .description("Total number of cache misses")
                .register(registry);
    }

    // Database related metrics
    @Bean
    public Timer databaseOperationTimer(MeterRegistry registry) {
        return Timer.builder("database_operation_time_seconds")
                .description("Time taken for database operations")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(10))
                .serviceLevelObjectives(
                        Duration.ofMillis(100), // 0.1s
                        Duration.ofMillis(500), // 0.5s
                        Duration.ofSeconds(1)   // 1s
                )
                .register(registry);
    }
}