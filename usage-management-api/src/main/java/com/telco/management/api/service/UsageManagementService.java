package com.telco.management.api.service;

import com.telco.common.dto.UsageUpdateRequest;
import com.telco.management.api.exception.BizException;  // 수정된 import 경로
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageManagementService {

    private final RabbitTemplate rabbitTemplate;

    public void updateUsage(UsageUpdateRequest request) {
        try {
            // 기본 검증
            if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
                throw new BizException("사용자 ID는 필수입니다", 400);
            }

            log.info("userId: {}, type: {}, amount: {}",
                    request.getUserId(), request.getType(), request.getAmount());

            rabbitTemplate.convertAndSend("usage.exchange", "usage.update", request);

            log.info("Successfully sent usage update message to queue");
        } catch (AmqpException e) {
            log.error("Failed to send message to queue", e);
            throw e;
        }
    }
}