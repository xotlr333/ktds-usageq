package com.telco.common.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CommonMetricsConfig {

    // Usage related metrics
    @Bean
    public Timer usageOperationTimer(MeterRegistry registry) {
        return Timer.builder("usage_operation_time")
                .description("Time taken for usage operations")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .serviceLevelObjectives(
                        Duration.ofMillis(100),
                        Duration.ofMillis(500),
                        Duration.ofSeconds(1)
                )
                .register(registry);
    }

    @Bean
    public Counter usageOperationCounter(MeterRegistry registry) {
        return Counter.builder("usage_operations_total")
                .description("Total number of usage operations")
                .register(registry);
    }

    @Bean
    public Counter usageOperationErrorCounter(MeterRegistry registry) {
        return Counter.builder("usage_operation_errors_total")
                .description("Total number of usage operation errors")
                .register(registry);
    }

    // Cache related metrics
    @Bean
    public Timer cacheOperationTimer(MeterRegistry registry) {
        return Timer.builder("cache_operation_time")
                .description("Time taken for cache operations")
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
        return Timer.builder("database_operation_time")
                .description("Time taken for database operations")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .serviceLevelObjectives(
                        Duration.ofMillis(100),
                        Duration.ofMillis(500),
                        Duration.ofSeconds(1)
                )
                .register(registry);
    }
}