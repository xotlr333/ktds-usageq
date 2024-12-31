package com.telco.common.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "usages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String userId;

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
    public Usage(String userId, VoiceUsage voiceUsage, VideoUsage videoUsage,
                 MessageUsage messageUsage, DataUsage dataUsage) {
        this.userId = userId;
        this.voiceUsage = voiceUsage;
        this.videoUsage = videoUsage;
        this.messageUsage = messageUsage;
        this.dataUsage = dataUsage;
    }

    public void updateUsage(String type, long amount) {
        switch (type) {
            case "VOICE" -> voiceUsage.addUsage(amount) ;
            case "VIDEO" -> videoUsage.addUsage(amount);
            case "MESSAGE" -> messageUsage.addUsage(amount);
            case "DATA" -> dataUsage.addUsage(amount);
            default -> throw new IllegalArgumentException("Invalid usage type: " + type);
        }
    }
}