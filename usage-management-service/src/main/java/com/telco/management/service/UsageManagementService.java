// File: usage-management-service/src/main/java/com/telco/management/service/UsageManagementService.java
package com.telco.management.service;

import com.telco.common.dto.UsageUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageManagementService {

    private final RabbitTemplate rabbitTemplate;

    public void updateUsage(UsageUpdateRequest request) {
        try {
            log.info("Sending usage update message to queue - userId: {}, type: {}, amount: {}",
                    request.getUserId(), request.getType(), request.getAmount());

            rabbitTemplate.convertAndSend("usage.exchange", "usage.update", request);

            log.info("Successfully sent usage update message to queue");
        } catch (Exception e) {
            log.error("Failed to send usage update message to queue - error: {}", e.getMessage());
            throw new RuntimeException("Failed to process usage update", e);
        }
    }
}