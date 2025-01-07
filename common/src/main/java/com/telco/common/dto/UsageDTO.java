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
    private String prodNm;  // 요금제명 필드 추가
    private UsageDetail voiceUsage;
    private UsageDetail videoUsage;
    private UsageDetail messageUsage;
    private UsageDetail dataUsage;
}
