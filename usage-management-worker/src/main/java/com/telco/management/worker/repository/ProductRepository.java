package com.telco.management.worker.repository;

import com.telco.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsByProdId(String prodId);
}