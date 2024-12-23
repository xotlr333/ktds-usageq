package com.telco.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class QueueStatus {
    private long queueSize;
    private long deadLetterQueueSize;
    private long processedCount;
    private long failureCount;
}

