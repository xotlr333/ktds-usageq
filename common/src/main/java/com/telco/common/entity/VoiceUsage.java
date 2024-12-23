package com.telco.common.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoiceUsage {
    private long totalUsage;
    private long freeUsage;
    private long excessUsage;

    @Builder
    public VoiceUsage(long totalUsage, long freeUsage) {
        this.totalUsage = totalUsage;
        this.freeUsage = freeUsage;
        calculateExcessUsage();
    }

    public void addUsage(long amount) {
        this.totalUsage += amount;
        calculateExcessUsage();
    }

    private void calculateExcessUsage() {
        this.excessUsage = Math.max(0, totalUsage - freeUsage);
    }
}