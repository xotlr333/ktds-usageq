package com.telco.management.api.config;

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
                .description("Time taken for usage update API operations")
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