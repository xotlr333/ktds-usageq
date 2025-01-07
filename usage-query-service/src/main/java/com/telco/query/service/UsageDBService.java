package com.telco.query.service;

import com.telco.common.entity.Usage;
import com.telco.common.entity.Product;
import com.telco.common.dto.UsageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.telco.query.mapper.UsageMapper;
import com.telco.query.repository.UsageRepository;
import com.telco.query.repository.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsageDBService {

    private final UsageRepository usageRepository;
    private final ProductRepository productRepository;
    private final UsageMapper usageMapper;

    @Transactional(readOnly = true)
    public Optional<UsageDTO> findByUserId(String userId) {
        return usageRepository.findByUserId(userId)
                .map(usage -> {
                    // prodId로 Product 정보 조회
                    Optional<Product> product = productRepository.findByProdId(usage.getProdId());
                    // Product 정보가 있는 경우에만 설정
                    product.ifPresent(usage::setProduct);
                    return usageMapper.toDTO(usage);
                });
    }
}