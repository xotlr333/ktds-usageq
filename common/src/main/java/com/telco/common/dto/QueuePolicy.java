package com.telco.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class QueuePolicy {
    private int maxRetryCount;
    private int retryIntervalSeconds;
}