package com.telco.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageDetail {
    private long totalUsage;
    private long freeUsage;
    private long excessUsage;
    private String unit;
}
