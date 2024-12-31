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
        setTotalUsage(totalUsage);  // 생성자에서도 동일한 로직 사용
        this.freeUsage = freeUsage;
    }

    // 새로운 메서드 - 값 설정
    public void setTotalUsage(long amount) {
        this.totalUsage = amount;  // 단순 대체
        this.excessUsage = Math.max(0, this.totalUsage - this.freeUsage);
    }
}