package com.telco.query.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.query")
public class DBDelayProperties {
    private volatile long delayMillis = 0; // 기본값 0ms
}