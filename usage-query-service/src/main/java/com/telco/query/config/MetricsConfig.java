package com.telco.query.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MetricsConfig {

    // 사용량 조회 관련 메트릭
    @Bean
    public Timer usageQueryTimer(MeterRegistry registry) {
        return Timer.builder("usage_query_time")
                .description("사용량 조회 소요 시간")
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
    public Counter usageRequestCounter(MeterRegistry registry) {
        return Counter.builder("usage_requests_total")
                .description("전체 사용량 조회 요청 수")
                .register(registry);
    }

    // 캐시 관련 메트릭
    @Bean
    public Counter cacheHitCounter(MeterRegistry registry) {
        return Counter.builder("cache_hits_total")
                .description("캐시 히트 수")
                .register(registry);
    }

    @Bean
    public Counter cacheMissCounter(MeterRegistry registry) {
        return Counter.builder("cache_misses_total")
                .description("캐시 미스 수")
                .register(registry);
    }

    @Bean
    public Timer cacheOperationTimer(MeterRegistry registry) {
        return Timer.builder("cache_operation_time")
                .description("캐시 작업 소요 시간")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(
                        Duration.ofMillis(50),
                        Duration.ofMillis(100),
                        Duration.ofMillis(200)
                )
                .register(registry);
    }

    // 데이터베이스 관련 메트릭
    @Bean
    public Timer databaseOperationTimer(MeterRegistry registry) {
        return Timer.builder("database_operation_time")
                .description("데이터베이스 작업 소요 시간")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .sla(
                        Duration.ofMillis(100),
                        Duration.ofMillis(500),
                        Duration.ofSeconds(1)
                )
                .register(registry);
    }
}