package com.telco.query.config;

import com.telco.common.dto.UsageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@EnableCaching
@Configuration
public class CacheConfig {

    @Value("${spring.redis.ttl:600}")
    private long redisTtl;

    @Value("${spring.redis.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${spring.redis.retry.backoff:1000}")
    private long retryBackoff;

    @Bean
    public RedisTemplate<String, UsageDTO> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UsageDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value serializers
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();

        // BackOff 정책 설정
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(retryBackoff);
        template.setBackOffPolicy(backOffPolicy);

        // Retry 정책 설정
        RetryPolicy retryPolicy = new SimpleRetryPolicy(maxRetryAttempts);
        template.setRetryPolicy(retryPolicy);

        return template;
    }
}