package com.telco.query.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.telco.common.config.CommonMetricsConfig;

import java.time.Duration;

@Configuration
@Import(CommonMetricsConfig.class)
public class MetricsConfig {
    // Query 서비스 전용 메트릭스만 추가
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

    @Bean
    public Counter usageRequestCounter(MeterRegistry registry) {
        return Counter.builder("usage_query_requests_total")
                .description("Total number of usage query requests")
                .register(registry);
    }
}