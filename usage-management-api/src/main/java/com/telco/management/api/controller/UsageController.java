package com.telco.management.api.controller;

import com.telco.common.dto.ApiResponse;
import com.telco.common.dto.UsageUpdateRequest;
import com.telco.management.api.service.UsageManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "사용량 관리 API", description = "사용량 업데이트 관련 API")
@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class UsageController {

    private final UsageManagementService usageManagementService;

    @Operation(summary = "사용량 업데이트", description = "사용자의 사용량을 실시간으로 업데이트합니다.")
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateUsage(@RequestBody UsageUpdateRequest request) {
        log.info("Received usage update request - userId: {}, type: {}, amount: {}",
                request.getUserId(), request.getType(), request.getAmount());

        usageManagementService.updateUsage(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/test/sequential/{userId}")
    public ResponseEntity<ApiResponse<Void>> testSequentialUpdates(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int count) {

        for (int i = 0; i < count; i++) {
            UsageUpdateRequest request = new UsageUpdateRequest();
            request.setUserId(userId);
            request.setType("V"); // Voice type
            request.setAmount(i + 1); // 순차적으로 증가하는 값

            usageManagementService.updateUsage(request);

            // 각 요청 사이에 약간의 간격을 둠
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}