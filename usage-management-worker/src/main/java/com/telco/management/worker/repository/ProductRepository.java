package com.telco.management.worker.repository;

import java.util.Optional;
import com.telco.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsByProdId(String prodId);

    @Query("SELECT p FROM Product p WHERE p.prodId = :prodId")
    Optional<Product> findByProdId(String prodId);
}