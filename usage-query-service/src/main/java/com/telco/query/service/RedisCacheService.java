package com.telco.query.service;

import com.telco.common.dto.CacheStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService<T> implements ICacheService<T> {

    private final RedisTemplate<String, T> redisTemplate;
    private final RetryTemplate retryTemplate;

    @Value("${spring.redis.ttl:600}")
    private long redisTtl;

    @Override
    public Optional<T> get(String key) {
        try {
            return retryTemplate.execute(context -> {
                T value = redisTemplate.opsForValue().get(key);
                if (value != null) {
                    redisTemplate.expire(key, redisTtl, TimeUnit.SECONDS);
                }
                return Optional.ofNullable(value);
            });
        } catch (Exception e) {
            log.error("Failed to get value from cache - key: {}, error: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void set(String key, T value) {
        try {
            retryTemplate.execute(context -> {
                redisTemplate.opsForValue().set(key, value, redisTtl, TimeUnit.SECONDS);
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to set value to cache - key: {}, error: {}", key, e.getMessage());
        }
    }

    @Override
    public void delete(String key) {
        try {
            retryTemplate.execute(context -> {
                redisTemplate.delete(key);
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to delete value from cache - key: {}, error: {}", key, e.getMessage());
        }
    }

    @Override
    public CacheStatus getStatus() {
        try {
            Long size = redisTemplate.getConnectionFactory().getConnection().dbSize();
            return CacheStatus.builder()
                    .totalSize(size != null ? size : 0L)
                    .usedSize(Optional.ofNullable(redisTemplate.keys("*")).map(Set::size).orElse(0))
                    .hitCount(getMetricFromInfo("keyspace_hits"))
                    .missCount(getMetricFromInfo("keyspace_misses"))
                    .build();
        } catch (Exception e) {
            log.error("Failed to get cache status: {}", e.getMessage());
            return CacheStatus.builder()
                    .totalSize(0L)
                    .usedSize(0L)
                    .hitCount(0L)
                    .missCount(0L)
                    .build();
        }
    }

    private long getMetricFromInfo(String metricName) {
        try {
            Properties info = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .info("stats");

            if (info == null) return 0L;

            String value = info.getProperty(metricName);
            if (value == null) return 0L;

            return Long.parseLong(value.trim());
        } catch (Exception e) {
            log.error("Failed to get metric {}: {}", metricName, e.getMessage());
            return 0L;
        }
    }
}