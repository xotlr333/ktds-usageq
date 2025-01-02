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

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageQueueConsumer {

    private final UsageRepository usageRepository;
    private final ICacheService<UsageDTO> cacheService;
    private final UsageMapper usageMapper;

    @RabbitListener(queues = "usage.queue",
            containerFactory = "rabbitListenerContainerFactory",
            returnExceptions = "false")
    @Transactional
    public void processUsageUpdate(UsageUpdateRequest request) {
        log.info("Received usage update request - userId: {}, type: {}, amount: {}",
                request.getUserId(), request.getType(), request.getAmount());

        try {
            Usage usage = usageRepository.findByUserIdWithLock(request.getUserId())
                    .orElseGet(() -> createNewUsage(request.getUserId()));

            updateUsageByType(usage, request.getType(), request.getAmount());
            usageRepository.save(usage);

            updateCache(usage);

            log.info("Successfully processed usage update for userId: {}", request.getUserId());

        } catch (Exception e) {
            log.error("Failed to process usage update for userId: {}, error: {}",
                    request.getUserId(), e.getMessage());
            throw e;
        }
    }

    private void updateUsageByType(Usage usage, String type, long amount) {
        switch (type.toUpperCase()) {
            case "VOICE" -> {
                if (usage.getVoiceUsage() == null) {
                    usage.setVoiceUsage(VoiceUsage.builder()
                            .totalUsage(amount)
                            .freeUsage(18000L)
                            .build());
                } else {
                    usage.getVoiceUsage().addUsage(amount);
                }
            }
            case "VIDEO" -> {
                if (usage.getVideoUsage() == null) {
                    usage.setVideoUsage(VideoUsage.builder()
                            .totalUsage(amount)
                            .freeUsage(7200L)
                            .build());
                } else {
                    usage.getVideoUsage().addUsage(amount);
                }
            }
            case "MESSAGE" -> {
                if (usage.getMessageUsage() == null) {
                    usage.setMessageUsage(MessageUsage.builder()
                            .totalUsage(amount)
                            .freeUsage(300L)
                            .build());
                } else {
                    usage.getMessageUsage().addUsage(amount);
                }
            }
            case "DATA" -> {
                if (usage.getDataUsage() == null) {
                    usage.setDataUsage(DataUsage.builder()
                            .totalUsage(amount)
                            .freeUsage(5368709120L) // 5GB in bytes
                            .build());
                } else {
                    usage.getDataUsage().addUsage(amount);
                }
            }
            default -> throw new IllegalArgumentException("Invalid usage type: " + type);
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