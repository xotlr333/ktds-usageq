package com.telco.query.repository;

import java.util.Optional;
import com.telco.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    @Query("SELECT p FROM Product p WHERE p.prodId = :prodId")
    Optional<Product> findByProdId(String prodId);
}