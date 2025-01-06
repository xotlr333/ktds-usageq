package com.telco.management.api.service;

import com.telco.common.entity.Usage;
import com.telco.common.dto.UsageUpdateRequest;
import com.telco.management.api.mapper.UsageMapper;
import com.telco.management.api.exception.BizException;  // 수정된 import 경로
import com.telco.management.api.repository.ProductRepository;
import com.telco.management.api.repository.UsageRepository;
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
    private final UsageRepository usageRepository;
    private final ProductRepository productRepository;

    public void updateUsage(UsageUpdateRequest request) {
        try {
            // 기본 검증
            if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
                throw new BizException("사용자 ID는 필수입니다", 400);
            }

            log.info("userId: {}, type: {}, amount: {}",
                    request.getUserId(), request.getType(), request.getAmount());

            Usage usage = usageRepository.findByUserIdWithLock(request.getUserId());

            if (usage != null) {
                // 상품 존재 여부만 체크
                if (!productRepository.existsByProdId(usage.getProdId())) {
                    log.warn("<<존재하지 않는 상품번호 입니다.>> - Invalid product requested - userId: {}, prodId: {}",
                            request.getUserId(), usage.getProdId());
                }else {
                    rabbitTemplate.convertAndSend("usage.exchange", "usage.update", request);
                }

                log.info("Successfully sent usage update message to queue");
            }else{
                log.warn("<<유효하지 않은 회선번호 입니다.>> - Invalid user requested - userId: {}", request.getUserId());
            }
        } catch (AmqpException e) {
            log.error("Failed to send message to queue", e);
            throw e;
        }
    }
}