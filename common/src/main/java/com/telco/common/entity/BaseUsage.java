package com.telco.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseUsage {

    private static final long UNLIMITED_FREE_USAGE = 999_999_999L;

    @Column(name = "total_usage")
    protected long totalUsage;

    @Column(name = "free_usage")
    protected long freeUsage;

    @Column(name = "excess_usage")
    protected long excessUsage;

    protected BaseUsage(long totalUsage, long freeUsage) {
        this.totalUsage = totalUsage;
        this.freeUsage = freeUsage;
        calculateExcessUsage();
    }

    public void addUsage(long amount) {
        this.totalUsage = amount;
        calculateExcessUsage();
    }

    private void calculateExcessUsage() {
        // 무제한인 경우 초과 사용량은 0으로 설정
        if (this.freeUsage == UNLIMITED_FREE_USAGE) {
            this.excessUsage = 0;
        } else {
            this.excessUsage = Math.max(0, totalUsage - freeUsage);
        }
    }

    protected boolean isUnlimited() {
        return this.freeUsage == UNLIMITED_FREE_USAGE;
    }
}