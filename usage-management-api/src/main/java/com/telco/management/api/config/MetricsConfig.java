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
    // API 서비스 전용 메트릭스만 추가
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
    public Timer apiResponseTimer(MeterRegistry registry) {
        return Timer.builder("api_response_time")
                .description("Time taken for API responses")
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