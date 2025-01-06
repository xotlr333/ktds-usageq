package com.telco.query.service;

import com.telco.common.dto.ApiResponse;
import com.telco.common.dto.UsageDTO;
import com.telco.common.exception.InvalidUserException;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageQueryServiceImpl implements IUsageQueryService {

    private final ICacheService<UsageDTO> cacheService;
    private final UsageDBService usageDBService;

    // Metrics
    private final Timer usageQueryTimer;
    private final Counter usageRequestCounter;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Timer cacheOperationTimer;
    private final Timer databaseOperationTimer;

    @Override
    public ResponseEntity<ApiResponse<UsageDTO>> getUserUsage(String userId) {
        Timer.Sample totalTimer = Timer.start();

        try {
            usageRequestCounter.increment();

            // 1. 캐시에서 조회 시도
            Timer.Sample cacheTimer = Timer.start();
            Optional<UsageDTO> cachedUsage = cacheService.get(formatCacheKey(userId));
            cacheTimer.stop(cacheOperationTimer);

            if (cachedUsage.isPresent()) {
                cacheHitCounter.increment();
                log.info("Cache Hit - userId: {}", userId);
                totalTimer.stop(usageQueryTimer);
                return ResponseEntity.ok(ApiResponse.success(cachedUsage.get()));
            }

            cacheMissCounter.increment();
            log.info("Cache Miss - userId: {}", userId);

            // 2. Cache Miss인 경우 DB에서 조회
            Timer.Sample dbTimer = Timer.start();
            Optional<UsageDTO> usage = usageDBService.findByUserId(userId);
            dbTimer.stop(databaseOperationTimer);

            // 존재하지 않는 회선번호인 경우
            if (usage.isEmpty()) {
                log.warn("<<유효하지 않는 회선입니다.>> - Invalid user requested - userId: {}", userId);
                totalTimer.stop(usageQueryTimer);
                return ResponseEntity.ok(ApiResponse.success(UsageDTO.builder()
                        .userId(userId)
                        .build()));
            }

            // 3. 조회된 데이터를 캐시에 저장
            Timer.Sample cacheUpdateTimer = Timer.start();
            try {

                cacheService.set(formatCacheKey(userId), usage.get());
                log.info("Cache Update - userId: {}", userId);
            } catch (Exception e) {
                log.error("Failed to update cache - userId: {}, error: {}", userId, e.getMessage());
            }
            cacheUpdateTimer.stop(cacheOperationTimer);

            totalTimer.stop(usageQueryTimer);
            return ResponseEntity.ok(ApiResponse.success(usage.get()));

        } catch (InvalidUserException e) {
            totalTimer.stop(usageQueryTimer);
            log.error("Invalid user requested - userId: {}", userId);
            return ResponseEntity
                    .status(404)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            totalTimer.stop(usageQueryTimer);
            log.error("Error while getting usage data - userId: {}, error: {}", userId, e.getMessage());
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error(500, "사용량 조회 중 오류가 발생했습니다."));
        }
    }

    private String formatCacheKey(String userId) {
        return String.format("usage:%s", userId);
    }
}