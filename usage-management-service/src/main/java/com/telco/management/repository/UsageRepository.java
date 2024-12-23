package com.telco.management.repository;

import com.telco.common.entity.Usage;  // 명시적 import 추가
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

/**
 * 사용량 데이터 저장소
 */
@Repository
public interface UsageRepository extends JpaRepository<Usage, Long> {
    Optional<Usage> findByUserId(String userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Usage u WHERE u.userId = :userId")
    Optional<Usage> findByUserIdWithLock(@Param("userId") String userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usage u WHERE u.userId = :userId")
    boolean existsByUserId(@Param("userId") String userId);
}