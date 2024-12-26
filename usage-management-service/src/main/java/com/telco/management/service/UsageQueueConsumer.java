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
public class UsageQueueConsumer {

    private final UsageRepository usageRepository;
    private final ICacheService<UsageDTO> cacheService;
    private final UsageMapper usageMapper;

    @RabbitListener(queues = "usage.queue")
    @Transactional
    public void processUsageUpdate(UsageUpdateRequest request) {
        log.info("Received usage update request for userId: {}", request.getUserId());

        try {
            Usage usage = usageRepository.findByUserIdWithLock(request.getUserId())
                    .orElseGet(() -> createNewUsage(request.getUserId()));

            usage.updateUsage(request.getType(), request.getAmount());
            usageRepository.save(usage);

            updateCache(usage);

            log.info("Successfully processed usage update cache for userId: {}", request.getUserId());

        } catch (Exception e) {
            log.error("Failed to process usage update cache for userId: {}, error: {}",
                    request.getUserId(), e.getMessage());
            throw e;
        }
    }

    private Usage createNewUsage(String userId) {
        return Usage.builder()
                .userId(userId)
                .voiceUsage(VoiceUsage.builder().totalUsage(0L).freeUsage(18000L).build())
                .videoUsage(VideoUsage.builder().totalUsage(0L).freeUsage(7200L).build())
                .messageUsage(MessageUsage.builder().totalUsage(0L).freeUsage(300L).build())
                .dataUsage(DataUsage.builder().totalUsage(0L).freeUsage(5368709120L).build())
                .build();
    }

    private void updateCache(Usage usage) {
        try {
            UsageDTO usageDTO = usageMapper.toDTO(usage);
            String cacheKey = String.format("usage:%s", usage.getUserId());
            cacheService.set(cacheKey, usageDTO);
        } catch (Exception e) {
            log.error("Failed to update cache for userId: {}, error: {}",
                    usage.getUserId(), e.getMessage());
        }
    }
}