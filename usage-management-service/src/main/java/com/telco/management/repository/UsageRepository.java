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
public interface UsageRepository extends JpaRepository<Usage, Long> {
    // 1. 인덱스 활용을 위한 쿼리 힌트 추가
    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "50"),
            @QueryHint(name = HINT_COMMENT, value = "Use Index")
    })
    Optional<Usage> findByUserId(String userId);

    // 2. N+1 문제 해결을 위한 페치 조인
    @Query("SELECT DISTINCT u FROM Usage u " +
            "LEFT JOIN FETCH u.voiceUsage " +
            "LEFT JOIN FETCH u.videoUsage " +
            "LEFT JOIN FETCH u.messageUsage " +
            "LEFT JOIN FETCH u.dataUsage " +
            "WHERE u.userId = :userId")
    Optional<Usage> findByUserIdWithUsages(@Param("userId") String userId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Usage u WHERE u.userId = :userId")
    Optional<Usage> findByUserIdWithLock(@Param("userId") String userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usage u WHERE u.userId = :userId")
    boolean existsByUserId(@Param("userId") String userId);
}
