package com.telco.management.service;

import com.telco.common.dto.UsageUpdateRequest;
import com.telco.common.entity.Usage;
import com.telco.common.exception.InvalidProductException;
import com.telco.common.exception.InvalidUserException;
import com.telco.management.repository.ProductRepository;
import com.telco.management.repository.UsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageManagementService {

    private final RabbitTemplate rabbitTemplate;
    private final UsageRepository usageRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public void updateUsage(UsageUpdateRequest request) {
        try {
            log.info("userId: {}, type: {}, amount: {}",
                    request.getUserId(), request.getType(), request.getAmount());

            Usage usage = usageRepository.findByUserIdWithLock(request.getUserId());

            if (usage != null) {
                // 상품 존재 여부만 체크
                String prodId = usage.getProdId();
                if (!productRepository.existsByProdId(prodId)) {
                    log.warn("<<존재하지 않는 상품번호 입니다.>> - Invalid product requested - userId: {}, prodId: {}",
                            request.getUserId(), prodId);
                }

                rabbitTemplate.convertAndSend("usage.exchange", "usage.update", request);

                log.info("Successfully sent usage update message to queue");
            }else{
                log.warn("<<유효하지 않은 회선번호 입니다.>> - Invalid user requested - userId: {}", request.getUserId());
            }

        } catch (Exception e) {
            log.error("Failed to send usage update message to queue - error: {}", e.getMessage());
            throw new RuntimeException("Failed to process usage update", e);
        }
    }
}