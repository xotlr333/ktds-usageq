package com.telco.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class DBStatus {
    private long connectionCount;
    private long activeQueries;
    private String status;
}