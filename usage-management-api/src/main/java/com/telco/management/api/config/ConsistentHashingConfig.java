package com.telco.management.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsistentHashingConfig {

    @Value("${app.queue.partitions:8}")
    private int partitionCount;

    public String getQueueName(String userId) {
        int partition = Math.abs(userId.hashCode() % partitionCount);
        return String.format("usage.queue.%d", partition);
    }
}