package com.telco.query.service;

import com.telco.common.entity.Usage;
import com.telco.common.dto.UsageDTO;
import com.telco.common.exception.BizException;
import com.telco.query.mapper.UsageMapper;
import com.telco.query.repository.UsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsageDBService {
    
    private final UsageRepository usageRepository;
    private final UsageMapper usageMapper;
    
    @Transactional(readOnly = true)
    public UsageDTO findByUserId(String userId) {
        Usage usage = usageRepository.findByUserId(userId)
                .orElseThrow(() -> new BizException("사용자 사용량 정보를 찾을 수 없습니다.", 404));
        return usageMapper.toDTO(usage);
    }
    
    @Transactional
    public void updateUsage(String userId, String type, long amount) {
        Usage usage = usageRepository.findByUserIdWithUsages(userId)
                .orElseThrow(() -> new BizException("사용자 사용량 정보를 찾을 수 없습니다.", 404));
        
        usage.updateUsage(type, amount);
    }
}
