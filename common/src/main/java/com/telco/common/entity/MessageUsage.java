package com.telco.common.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageUsage {
    private static final long UNLIMITED_FREE_USAGE = 999_999_999L;

    private long totalUsage;
    private long freeUsage;
    private long excessUsage;

    @Builder
    public MessageUsage(long totalUsage, long freeUsage) {
        this.totalUsage = totalUsage;
        this.freeUsage = freeUsage;
        calculateExcessUsage();
    }

    public void addUsage(long amount) {
        this.totalUsage = amount;
        calculateExcessUsage();
    }

    private void calculateExcessUsage() {
        if (this.freeUsage == UNLIMITED_FREE_USAGE) {
            this.excessUsage = 0;
        } else {
            this.excessUsage = Math.max(0, totalUsage - freeUsage);
        }
    }
}