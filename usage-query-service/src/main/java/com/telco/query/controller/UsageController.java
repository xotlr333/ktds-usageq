package com.telco.query.controller;

import com.telco.common.dto.ApiResponse;
import com.telco.common.dto.UsageDTO;
import com.telco.query.service.IUsageQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용량 조회 API", description = "사용자의 사용량 정보를 조회하는 API")
@RestController
@RequestMapping("/api/usage")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsageController {
    private final IUsageQueryService usageQueryService;

    @Operation(summary = "사용량 조회", description = "사용자의 음성/영상/문자/데이터 사용량을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UsageDTO>> getUserUsage(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable String userId) {
        return usageQueryService.getUserUsage(userId);
    }
}