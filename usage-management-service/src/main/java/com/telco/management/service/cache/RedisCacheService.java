package com.telco.management.service.cache;

import com.telco.common.dto.CacheStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
            redisTemplate.opsForValue().set(key, value, redisTtl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to set value to cache - key: {}, error: {}", key, e.getMessage());
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Failed to delete value from cache - key: {}, error: {}", key, e.getMessage());
        }
    }

    @Override
    public CacheStatus getStatus() {
        try {
            Long dbSize = redisTemplate.getConnectionFactory().getConnection().dbSize();
            Long keyCount = Optional.ofNullable(redisTemplate.keys("*"))
                    .map(keys -> (long) keys.size())
                    .orElse(0L);

            return CacheStatus.builder()
                    .totalSize(dbSize)
                    .usedSize(keyCount)
                    .hitCount(0L) // Redis INFO 명령어 결과 파싱 대신 기본값 사용
                    .missCount(0L)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get cache status: {}", e.getMessage());
            return CacheStatus.builder().build();
        }
    }
}
