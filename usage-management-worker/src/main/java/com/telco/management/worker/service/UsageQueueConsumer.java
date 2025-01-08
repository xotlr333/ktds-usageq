package com.telco.management.worker.service;

import com.rabbitmq.client.Channel;
import com.telco.common.dto.UsageDTO;
import com.telco.common.dto.UsageUpdateRequest;
import com.telco.common.entity.*;
import com.telco.management.worker.mapper.UsageMapper;
import com.telco.management.worker.repository.ProductRepository;
import com.telco.management.worker.repository.UsageRepository;
import com.telco.management.worker.service.cache.ICacheService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageQueueConsumer {

    private final UsageRepository usageRepository;
    private final ICacheService<UsageDTO> cacheService;
    private final UsageMapper usageMapper;
    private final ProductRepository productRepository;

    // 메트릭스 추가
    private final Timer usageUpdateTimer;
    private final Timer cacheUpdateTimer;
    private final Counter usageUpdateSuccessCounter;
    private final Counter usageUpdateFailureCounter;
    private final Counter usageInvalidErrorCounter ;

    // Consumer 그룹별 처리를 위한 ConcurrentMap 추가
    private final ConcurrentMap<Integer, Lock> userLocks = new ConcurrentHashMap<>();

    @RabbitListener(queues = "usage.queue",
            containerFactory = "rabbitListenerContainerFactory",
            returnExceptions = "false")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processUsageUpdate(UsageUpdateRequest request,
                                   Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            log.info("Processing usage update - userId: {}, type: {}, amount: {}",
                    request.getUserId(), request.getType(), request.getAmount());

            Usage usage = usageRepository.findByUserIdWithLock(request.getUserId());

            if (usage != null) {
                // 1. DB 업데이트
                UsageType usageType = UsageType.fromCode(request.getType());
                usage.updateUsage(usageType, request.getAmount());

                // Product 정보 조회 및 설정
                productRepository.findByProdId(usage.getProdId())
                        .ifPresent(usage::setProduct);

                Usage savedUsage = usageRepository.save(usage);

                // 2. 캐시 업데이트
                try {
                    updateCache(savedUsage);
                } catch (Exception e) {
                    // 캐시 업데이트 실패는 로깅만 하고 진행
                    log.error("Cache update failed - userId: {}, error: {}",
                            request.getUserId(), e.getMessage());
                }

                // 3. 처리 완료 후 ACK
                channel.basicAck(tag, false);

                log.info("Successfully processed usage update - userId: {}, type: {}",
                        savedUsage.getUserId(), usageType);

            } else {
                // 유효하지 않은 사용자는 그냥 ACK
                channel.basicAck(tag, false);
                log.warn("Invalid user requested - userId: {}", request.getUserId());
            }

        } catch (Exception e) {
            try {
                // 처리 실패시 DLQ로 이동
                channel.basicNack(tag, false, false);
            } catch (IOException ex) {
                log.error("Error during nack", ex);
            }
            log.error("Failed to process usage update - userId: {}, error: {}",
                    request.getUserId(), e.getMessage());
            throw new RuntimeException("Failed to process usage update", e);
        }
    }

    private Lock getUserLock(String userId) {
        // 사용자 ID를 해시하여 특정 Lock에 매핑
        int bucketId = Math.abs(userId.hashCode() % 100); // 100개의 Lock bucket 사용
        log.info("userLock========================: {}", bucketId);
        return userLocks.computeIfAbsent(bucketId, k -> new ReentrantLock());
    }

    private void updateCache(Usage usage) {
        try {
            String cacheKey = String.format("usage:%s", usage.getUserId());
            cacheService.set(cacheKey, usageMapper.toDTO(usage));
        } catch (Exception e) {
            log.error("Failed to update cache - userId: {}, error: {}",
                    usage.getUserId(), e.getMessage());
            throw e;  // 캐시 업데이트 실패도 중요한 실패로 간주
        }
    }
}