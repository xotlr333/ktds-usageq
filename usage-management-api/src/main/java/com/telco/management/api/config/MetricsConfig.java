package com.telco.management.api.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public Timer usageUpdateTimer(MeterRegistry registry) {
        return Timer.builder("usage_update_time")
                .description("Time taken to process usage update")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(
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
}