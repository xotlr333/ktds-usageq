package com.telco.management.worker.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("${app.pod_name:unknown}")
    private String podName;

    @RabbitListener(queues = {
            "usage.queue.0", "usage.queue.1", "usage.queue.2", "usage.queue.3",
            "usage.queue.4", "usage.queue.5", "usage.queue.6", "usage.queue.7"},
            containerFactory = "rabbitListenerContainerFactory",
            returnExceptions = "false")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processUsageUpdate(UsageUpdateRequest request) {
        Timer.Sample workerusageUpdateTimer = Timer.start();

        log.info("Received message from queue: {}", request);
        try {
            Usage usage = usageRepository.findByUserIdWithLock(request.getUserId());

            if (usage != null) {
                String prodId = usage.getProdId();
                if (!productRepository.existsByProdId(prodId)) {
                    usageInvalidErrorCounter.increment();
                    log.warn(" No product <<존재하지 않는 상품번호 입니다.>> - Invalid product requested - userId: {}, prodId: {}",
                            request.getUserId(), prodId);
                } else {
                    UsageType usageType = UsageType.fromCode(request.getType());
                    usage.updateUsage(usageType, request.getAmount());

                    // Product 정보 조회 및 설정 추가
                    productRepository.findByProdId(usage.getProdId())
                            .ifPresent(usage::setProduct);

                    Usage savedUsage = usageRepository.save(usage);

                    log.info("====================={}", podName);

                    Timer.Sample workercacheUpdateTimer = Timer.start();
                    updateCache(savedUsage);
                    workercacheUpdateTimer.stop(cacheUpdateTimer);

                    usageUpdateSuccessCounter.increment();
                    log.info("Successfully processed usage update - userId: {}, type: {}, createdAt: {}, updatedAt: {}",
                            savedUsage.getUserId(), usageType, savedUsage.getCreatedAt(), savedUsage.getUpdatedAt());
                }
            } else {
                usageInvalidErrorCounter.increment();
                log.warn(" No user <<유효하지 않은 회선번호 입니다.>> - Invalid user requested - userId: {}", request.getUserId());
            }

        } catch (Exception e) {
            usageUpdateFailureCounter.increment();
            log.error("Failed to process usage update - userId: {}, error: {}",
                    request.getUserId(), e.getMessage());
            throw e;
        } finally {
            workerusageUpdateTimer.stop(usageUpdateTimer);
        }
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