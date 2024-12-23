package com.telco.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "시스템 정책 설정")
@Getter
@Setter
public class SystemPolicyDTO {
    private CachePolicy cachePolicy;
    private QueuePolicy queuePolicy;
    private SystemPolicy systemPolicy;
}

