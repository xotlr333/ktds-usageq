package com.telco.common.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usage {
    @Id
    @Column(nullable = false, length = 50)
    private String userId;    // id 삭제하고 userId를 PK로 변경

    @Column(name = "prod_id", nullable = false)    // offer_id -> prod_id로 변경
    private String prodId;

    @ManyToOne(fetch = FetchType.LAZY)    // 새로 추가: Product와의 관계 설정
    @JoinColumn(name = "prod_id", insertable = false, updatable = false)
    private Product product;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalUsage", column = @Column(name = "voice_total_usage")),
            @AttributeOverride(name = "freeUsage", column = @Column(name = "voice_free_usage")),
            @AttributeOverride(name = "excessUsage", column = @Column(name = "voice_excess_usage"))
    })
    private VoiceUsage voiceUsage;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalUsage", column = @Column(name = "video_total_usage")),
            @AttributeOverride(name = "freeUsage", column = @Column(name = "video_free_usage")),
            @AttributeOverride(name = "excessUsage", column = @Column(name = "video_excess_usage"))
    })
    private VideoUsage videoUsage;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalUsage", column = @Column(name = "message_total_usage")),
            @AttributeOverride(name = "freeUsage", column = @Column(name = "message_free_usage")),
            @AttributeOverride(name = "excessUsage", column = @Column(name = "message_excess_usage"))
    })
    private MessageUsage messageUsage;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalUsage", column = @Column(name = "data_total_usage")),
            @AttributeOverride(name = "freeUsage", column = @Column(name = "data_free_usage")),
            @AttributeOverride(name = "excessUsage", column = @Column(name = "data_excess_usage"))
    })
    private DataUsage dataUsage;

    @Builder
    public Usage(String userId, String prodId, VoiceUsage voiceUsage, VideoUsage videoUsage,
                 MessageUsage messageUsage, DataUsage dataUsage) {
        this.userId = userId;
        this.prodId = prodId;
        this.voiceUsage = voiceUsage;
        this.videoUsage = videoUsage;
        this.messageUsage = messageUsage;
        this.dataUsage = dataUsage;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void updateUsage(UsageType type, long amount) {
        switch (type) {
            case VOICE -> {
                voiceUsage.addUsage(amount);
                updateModifiedTime();
            }
            case VIDEO -> {
                videoUsage.addUsage(amount);
                updateModifiedTime();
            }
            case MESSAGE -> {
                messageUsage.addUsage(amount);
                updateModifiedTime();
            }
            case DATA -> {
                dataUsage.addUsage(amount);
                updateModifiedTime();
            }
        }
    }

    private void updateModifiedTime() {
        this.updatedAt = LocalDateTime.now();
    }
}