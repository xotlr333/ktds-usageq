package com.telco.query.config;

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
        return Timer.builder("usage_query_time")
                .description("Time taken for usage queries")
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