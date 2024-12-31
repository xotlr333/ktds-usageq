package com.telco.management.mapper;

import com.telco.common.dto.UsageDTO;
import com.telco.common.dto.UsageDetail;
import com.telco.common.entity.*;
import org.springframework.stereotype.Component;

@Component
public class UsageMapper {

    public UsageDTO toDTO(Usage usage) {
        if (usage == null) return null;

        return UsageDTO.builder()
                .userId(usage.getUserId())
                .voiceUsage(new UsageDetail(usage.getVoiceTotalUsage(),
                        usage.getVoiceFreeUsage(),
                        usage.getVoiceExcessUsage(), "초"))
                .videoUsage(new UsageDetail(usage.getVideoTotalUsage(),
                        usage.getVideoFreeUsage(),
                        usage.getVideoExcessUsage(), "초"))
                .messageUsage(new UsageDetail(usage.getMessageTotalUsage(),
                        usage.getMessageFreeUsage(),
                        usage.getMessageExcessUsage(), "건"))
                .dataUsage(new UsageDetail(usage.getDataTotalUsage(),
                        usage.getDataFreeUsage(),
                        usage.getDataExcessUsage(), "패킷"))
                .build();
    }
}