package com.telco.common.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "usages")
@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String userId;

    // 직접 값을 관리하도록 변경
    @Column(name = "voice_total_usage")
    private long voiceTotalUsage;

    @Column(name = "voice_free_usage")
    private long voiceFreeUsage;

    @Column(name = "voice_excess_usage")
    private long voiceExcessUsage;

    // 다른 사용량도 동일하게 변경
    @Column(name = "video_total_usage")
    private long videoTotalUsage;

    @Column(name = "video_free_usage")
    private long videoFreeUsage;

    @Column(name = "video_excess_usage")
    private long videoExcessUsage;

    @Column(name = "message_total_usage")
    private long messageTotalUsage;

    @Column(name = "message_free_usage")
    private long messageFreeUsage;

    @Column(name = "message_excess_usage")
    private long messageExcessUsage;

    @Column(name = "data_total_usage")
    private long dataTotalUsage;

    @Column(name = "data_free_usage")
    private long dataFreeUsage;

    @Column(name = "data_excess_usage")
    private long dataExcessUsage;

    @Builder
    public Usage(String userId) {
        this.userId = userId;
        this.voiceFreeUsage = 18000L;
        this.videoFreeUsage = 7200L;
        this.messageFreeUsage = 300L;
        this.dataFreeUsage = 5368709120L;
    }

    public void updateUsage(String type, long amount) {
        switch (type) {
            case "VOICE" -> {
                this.voiceTotalUsage = amount;
                this.voiceExcessUsage = Math.max(0, amount - voiceFreeUsage);
            }
            case "VIDEO" -> {
                this.videoTotalUsage = amount;
                this.videoExcessUsage = Math.max(0, amount - videoFreeUsage);
            }
            case "MESSAGE" -> {
                this.messageTotalUsage = amount;
                this.messageExcessUsage = Math.max(0, amount - messageFreeUsage);
            }
            case "DATA" -> {
                this.dataTotalUsage = amount;
                this.dataExcessUsage = Math.max(0, amount - dataFreeUsage);
            }
            default -> throw new IllegalArgumentException("Invalid usage type: " + type);
        }
    }
}