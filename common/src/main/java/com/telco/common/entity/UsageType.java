package com.telco.common.entity;

import lombok.Getter;

public enum UsageType {
    VOICE("V", 18000L),
    VIDEO("P", 7200L),
    MESSAGE("T", 300L),
    DATA("D", 5368709120L);

    private final String code;
    @Getter
    private final long freeUsage;

    UsageType(String code, long freeUsage) {
        this.code = code;
        this.freeUsage = freeUsage;
    }

    public static UsageType fromCode(String code) {
        for (UsageType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid usage type code: " + code);
    }

}
