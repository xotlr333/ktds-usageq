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
    // Worker 서비스 전용 메트릭스만 추가
    @Bean
    public Counter messageProcessCounter(MeterRegistry registry) {
        return Counter.builder("message_process_total")
                .description("Total number of processed messages")
                .register(registry);
    }

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

    @Bean
    public Timer messageProcessingTimer(MeterRegistry registry) {
        return Timer.builder("message_processing_time")
                .description("Time taken for message processing")
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