package com.telco.management.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisCacheService<T> {
    private final RedisTemplate<String, T> redisTemplate;
    private final long redisTtl;

    public RedisCacheService(
            RedisTemplate<String, T> redisTemplate,
            @Value("${spring.redis.ttl:600}") long redisTtl
    ) {
        this.redisTemplate = redisTemplate;
        this.redisTtl = redisTtl;
    }

    public Optional<T> get(String key) {
        try {
            T value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                redisTemplate.expire(key, redisTtl, TimeUnit.SECONDS);
            }
            return Optional.ofNullable(value);
        } catch (Exception e) {
            log.error("Failed to get value from cache - key: {}, error: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    public void set(String key, T value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, redisTtl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to set value to cache - key: {}, error: {}", key, e.getMessage());
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Failed to delete value from cache - key: {}, error: {}", key, e.getMessage());
        }
    }
}