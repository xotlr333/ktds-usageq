package com.telco.query.controller;

import com.telco.common.dto.ApiResponse;
import com.telco.query.config.DBDelayProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Query 딜레이 설정 API", description = "사용량 조회 시 딜레이 설정을 관리하는 API")
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class DelayConfigController {

    private final DBDelayProperties delayProperties;

    @Operation(summary = "조회 딜레이 조회", description = "현재 설정된 사용량 조회 딜레이 값을 조회합니다.")
    @GetMapping("/delay")
    public ResponseEntity<ApiResponse<Long>> getDelay() {
        return ResponseEntity.ok(ApiResponse.success(delayProperties.getDelayMillis()));
    }

    @Operation(summary = "조회 딜레이 설정", description = "사용량 조회 시 적용할 딜레이를 설정합니다.")
    @PutMapping("/delay/{millis}")
    public ResponseEntity<ApiResponse<Void>> setDelay(@PathVariable long millis) {
        delayProperties.setDelayMillis(millis);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}