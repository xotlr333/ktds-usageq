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
    private VoiceUsage voiceUsage;

    @Embedded
    private VideoUsage videoUsage;

    @Embedded
    private MessageUsage messageUsage;

    @Embedded
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
            case "VOICE" -> voiceUsage.addUsage(amount);
            case "VIDEO" -> videoUsage.addUsage(amount);
            case "MESSAGE" -> messageUsage.addUsage(amount);
            case "DATA" -> dataUsage.addUsage(amount);
            default -> throw new IllegalArgumentException("Invalid usage type: " + type);
        }
    }
}