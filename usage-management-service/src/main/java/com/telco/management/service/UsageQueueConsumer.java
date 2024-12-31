package com.telco.management.service;

import com.telco.common.dto.UsageDTO;
import com.telco.common.dto.UsageUpdateRequest;
import com.telco.common.entity.*;
import com.telco.management.mapper.UsageMapper;
import com.telco.management.repository.UsageRepository;
import com.telco.management.service.cache.ICacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsageQueueConsumer {

    private final UsageRepository usageRepository;
    private final ICacheService<UsageDTO> cacheService;
    private final UsageMapper usageMapper;

    @Transactional
    @RabbitListener(queues = "usage.queue")
    public void processUsageUpdate(UsageUpdateRequest request) {
        String userId = request.getUserId();
        log.info("Processing usage update - userId: {}, type: {}, amount: {}",
                userId, request.getType(), request.getAmount());

        try {
            // DB 업데이트
            Usage usage = usageRepository.findByUserIdWithLock(userId)
                    .orElseGet(() -> Usage.builder()
                            .userId(userId)
                            .build());

            usage.updateUsage(request.getType(), request.getAmount());
            Usage savedUsage = usageRepository.saveAndFlush(usage);

            // 캐시 업데이트 (트랜잭션 커밋 후)
            try {
                updateCache(savedUsage);
            } catch (Exception e) {
                log.error("Cache update failed - userId: {}", userId, e);
            }

        } catch (Exception e) {
            log.error("Failed to process usage update - userId: {}", userId, e);
            throw e;
        }
    }

    @Transactional
    protected Usage updateUsageInTransaction(UsageUpdateRequest request) {
        Usage usage = usageRepository.findByUserIdWithLock(request.getUserId())
                .orElseGet(() -> Usage.builder()
                        .userId(request.getUserId())
                        .build());

        usage.updateUsage(request.getType(), request.getAmount());
        return usageRepository.saveAndFlush(usage);
    }

    private void updateCache(Usage usage) {
        String cacheKey = String.format("usage:%s", usage.getUserId());
        UsageDTO usageDTO = usageMapper.toDTO(usage);
        cacheService.set(cacheKey, usageDTO);
    }
}