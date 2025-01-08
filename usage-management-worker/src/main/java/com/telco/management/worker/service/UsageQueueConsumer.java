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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    public void processUsageUpdate(UsageUpdateRequest request) {
        Timer.Sample workerusageUpdateTimer = Timer.start();
        String userId = request.getUserId();

        // 사용자별 Lock 획득
        Lock userLock = getUserLock(userId);
        boolean lockAcquired = false;

        log.info("Received message from queue: {}", request);
        try {
            // 최대 100ms 동안 Lock 획득 시도
            lockAcquired = userLock.tryLock(100, TimeUnit.MILLISECONDS);
            if (!lockAcquired) {
                throw new RuntimeException("Unable to acquire lock for user: " + userId);
            }

            log.info("Received message from queue: {}", request);

            Usage usage = usageRepository.findByUserIdWithLock(userId);

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
//            throw e;
        } finally {
            workerusageUpdateTimer.stop(usageUpdateTimer);
        }
    }

    private Lock getUserLock(String userId) {
        // 사용자 ID를 해시하여 특정 Lock에 매핑
        int bucketId = Math.abs(userId.hashCode() % 100); // 100개의 Lock bucket 사용
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