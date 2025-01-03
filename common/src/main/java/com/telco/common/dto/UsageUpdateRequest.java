package com.telco.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UsageUpdateRequest {
    private String userId;
    private String type;
    private long amount;
    private int retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String prodId; // 상품id
    
    public void incrementRetryCount() {
        this.retryCount++;
    }
}
