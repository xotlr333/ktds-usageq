package com.telco.query.service;

import com.telco.common.dto.ApiResponse;
import com.telco.common.dto.UsageDTO;
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

    @Override
    public ResponseEntity<ApiResponse<UsageDTO>> getUserUsage(String userId) {
        try {
            // 1. 캐시에서 조회 시도
            Optional<UsageDTO> cachedUsage = cacheService.get(formatCacheKey(userId));

            if (cachedUsage.isPresent()) {
                log.info("Cache Hit - userId: {}", userId);
                return ResponseEntity.ok(ApiResponse.success(cachedUsage.get()));
            }

            log.info("Cache Miss - userId: {}", userId);

            // 2. Cache Miss인 경우 DB에서 조회
            Optional<UsageDTO> usage = usageDBService.findByUserId(userId);

            if (usage.isEmpty()) {
                return ResponseEntity
                        .status(404)
                        .body(ApiResponse.error(404, "요청하신 사용자의 사용량 정보가 존재하지 않습니다."));
            }

            try {
                // 3. 조회된 데이터를 캐시에 저장
                cacheService.set(formatCacheKey(userId), usage.get());
                log.info("Cache Update - userId: {}", userId);
            } catch (Exception e) {
                log.error("Failed to update cache - userId: {}, error: {}", userId, e.getMessage());
            }

            return ResponseEntity.ok(ApiResponse.success(usage.get()));
        } catch (Exception e) {
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