package com.telco.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class SystemPolicy {
    private boolean autoScalingEnabled;
    private boolean traceEnabled;
}