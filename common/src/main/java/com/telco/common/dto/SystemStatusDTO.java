package com.telco.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "시스템 상태 정보")
@Getter
@Builder
public class SystemStatusDTO {
    @Schema(description = "캐시 상태")
    private CacheStatus cacheStatus;
    
    @Schema(description = "큐 상태")
    private QueueStatus queueStatus;
    
    @Schema(description = "DB 상태")
    private DBStatus dbStatus;
}





