package com.telco.management.service;

import com.telco.common.dto.UsageDTO;
import com.telco.common.dto.UsageUpdateRequest;
import com.telco.common.entity.*;
import com.telco.common.exception.InvalidProductException;
import com.telco.common.exception.InvalidUserException;
import com.telco.management.mapper.UsageMapper;
import com.telco.management.repository.ProductRepository;
import com.telco.management.repository.UsageRepository;
import com.telco.management.service.cache.ICacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processUsageUpdate(UsageUpdateRequest request) {
        log.info("Received usage update request - userId: {}, type: {}, amount: {}",
                request.getUserId(), request.getType(), request.getAmount());
//        Usage usage = null;

        try {
            Usage usage = usageRepository.findByUserIdWithLock(request.getUserId());

            UsageType usageType = UsageType.fromCode(request.getType());
            usage.updateUsage(usageType, request.getAmount());

            Usage savedUsage = usageRepository.save(usage);
            updateCache(savedUsage);

            log.info("Successfully processed usage update - userId: {}, type: {}, createdAt: {}, updatedAt: {}",
                    savedUsage.getUserId(), usageType, savedUsage.getCreatedAt(), savedUsage.getUpdatedAt());

        } catch (InvalidUserException e) {
            log.error("Invalid user for usage update - userId: {}, error: {}",
                    request.getUserId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to process usage update - userId: {}, error: {}",
                    request.getUserId(), e.getMessage());
            throw e;
        }
    }

    private Usage createNewUsage(String userId) {
        log.info("Creating new usage record for userId: {}", userId);
        return Usage.builder()
                .userId(userId)
                .voiceUsage(VoiceUsage.builder()
                        .totalUsage(0L)
                        .freeUsage(UsageType.VOICE.getFreeUsage())
                        .build())
                .videoUsage(VideoUsage.builder()
                        .totalUsage(0L)
                        .freeUsage(UsageType.VIDEO.getFreeUsage())
                        .build())
                .messageUsage(MessageUsage.builder()
                        .totalUsage(0L)
                        .freeUsage(UsageType.MESSAGE.getFreeUsage())
                        .build())
                .dataUsage(DataUsage.builder()
                        .totalUsage(0L)
                        .freeUsage(UsageType.DATA.getFreeUsage())
                        .build())
                .build();
    }

    private void updateCache(Usage usage) {
        try {
            String cacheKey = String.format("usage:%s", usage.getUserId());
            cacheService.set(cacheKey, usageMapper.toDTO(usage));
        } catch (Exception e) {
            log.error("Failed to update cache - userId: {}, error: {}",
                    usage.getUserId(), e.getMessage());
        }
    }
}