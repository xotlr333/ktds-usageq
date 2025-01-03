package com.telco.management.repository;

import com.telco.common.entity.Usage;  // 명시적 import 추가
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import static org.hibernate.jpa.HibernateHints.HINT_COMMENT;
import static org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE;

/**
 * 사용량 데이터 저장소
 */
@Repository
public interface UsageRepository extends JpaRepository<Usage, String> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Usage u WHERE u.userId = :userId")
    Usage findByUserIdWithLock(@Param("userId") String userId);

    // 또는 더 간단하게 JPA 기본 메서드 사용
    boolean existsByUserId(String userId);
}
