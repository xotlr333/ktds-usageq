package com.telco.query.service;

import com.telco.common.entity.Usage;
import com.telco.common.dto.UsageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.telco.query.mapper.UsageMapper;
import com.telco.query.repository.UsageRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsageDBService {

    private final UsageRepository usageRepository;
    private final UsageMapper usageMapper;

    @Transactional(readOnly = true)
    public Optional<UsageDTO> findByUserId(String userId) {
        return usageRepository.findByUserId(userId)
                .map(usageMapper::toDTO);
    }
}