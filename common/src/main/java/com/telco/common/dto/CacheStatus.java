package com.telco.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CacheStatus {
    private long totalSize;
    private long usedSize;
    private long hitCount;
    private long missCount;
}