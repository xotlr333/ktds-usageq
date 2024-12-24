package com.telco.query.service;

import com.telco.common.dto.CacheStatus;
import com.telco.common.dto.UsageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisCacheService implements ICacheService<UsageDTO> {
    private final RedisTemplate<String, UsageDTO> redisTemplate;
    private final long redisTtl;

    public RedisCacheService(
            RedisTemplate<String, UsageDTO> redisTemplate,
            @Value("${spring.redis.ttl:600}") long redisTtl
    ) {
        this.redisTemplate = redisTemplate;
        this.redisTtl = redisTtl;
    }

    @Override
    public Optional<UsageDTO> get(String key) {
        try {
            UsageDTO value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                redisTemplate.expire(key, redisTtl, TimeUnit.SECONDS);
            }
            return Optional.ofNullable(value);
        } catch (Exception e) {
            log.error("Failed to get value from cache - key: {}, error: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void set(String key, UsageDTO value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, redisTtl, TimeUnit.SECONDS);
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
            // int를 Long으로 명시적 변환
            Long keyCount = (long) redisTemplate.keys("*").size();

            return CacheStatus.builder()
                    .totalSize(dbSize)
                    .usedSize(keyCount)
                    .hitCount(0L)
                    .missCount(0L)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get cache status: {}", e.getMessage());
            return CacheStatus.builder().build();
        }
    }
}