package com.telco.query.config;

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
    public Timer usageQueryTimer(MeterRegistry registry) {
        return Timer.builder("usage_query_time_seconds")
                .description("Time taken for usage queries")
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
    public Counter usageRequestCounter(MeterRegistry registry) {
        return Counter.builder("usage_requests_total")
                .description("Total number of usage requests")
                .register(registry);
    }
}