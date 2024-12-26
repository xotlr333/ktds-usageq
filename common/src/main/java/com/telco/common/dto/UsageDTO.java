package com.telco.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageDTO {
    private String userId;
    private UsageDetail voiceUsage;
    private UsageDetail videoUsage;
    private UsageDetail messageUsage;
    private UsageDetail dataUsage;
}
