package com.telco.common.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CommonMetricsConfig {

    // Usage update related metrics
    @Bean
    public Timer usageUpdateTimer(MeterRegistry registry) {
        return Timer.builder("usage_update_time")
                .description("Time taken to process usage update")
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
    public Counter usageUpdateRequestCounter(MeterRegistry registry) {
        return Counter.builder("usage_update_requests_total")
                .description("Total number of usage update requests")
                .register(registry);
    }

    @Bean
    public Counter usageUpdateErrorCounter(MeterRegistry registry) {
        return Counter.builder("usage_update_errors_total")
                .description("Total number of usage update errors")
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
    public Timer cacheUpdateTimer(MeterRegistry registry) {
        return Timer.builder("cache_update_time")
                .description("Time taken for cache updates")
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

    // Queue related metrics
    @Bean
    public Counter queuePublishCounter(MeterRegistry registry) {
        return Counter.builder("queue_publish_total")
                .description("Total number of messages published to queue")
                .register(registry);
    }

    @Bean
    public Counter queuePublishErrorCounter(MeterRegistry registry) {
        return Counter.builder("queue_publish_errors_total")
                .description("Total number of queue publish errors")
                .register(registry);
    }

    @Bean
    public Counter messageProcessCounter(MeterRegistry registry) {
        return Counter.builder("message_process_total")
                .description("Total number of processed messages")
                .register(registry);
    }

    // Validation related metrics
    @Bean
    public Counter invalidUserCounter(MeterRegistry registry) {
        return Counter.builder("invalid_user_total")
                .description("Total number of invalid user requests")
                .register(registry);
    }

    @Bean
    public Counter invalidProductCounter(MeterRegistry registry) {
        return Counter.builder("invalid_product_total")
                .description("Total number of invalid product requests")
                .register(registry);
    }
}