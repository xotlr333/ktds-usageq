package com.telco.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class CachePolicy {
    private int ttlMinutes;
    private long maxSize;
}