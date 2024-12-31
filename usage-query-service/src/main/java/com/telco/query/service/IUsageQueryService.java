package com.telco.query.service;

import com.telco.common.dto.ApiResponse;
import com.telco.common.dto.UsageDTO;
import org.springframework.http.ResponseEntity;

public interface IUsageQueryService {
    ResponseEntity<ApiResponse<UsageDTO>> getUserUsage(String userId);
}