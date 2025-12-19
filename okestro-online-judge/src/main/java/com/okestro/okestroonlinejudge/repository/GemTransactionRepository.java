package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.GemTransactionEntity;
import com.okestro.okestroonlinejudge.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 젬 거래 이력 레포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface GemTransactionRepository extends JpaRepository<GemTransactionEntity, Long> {

    /**
     * 사용자의 젬 거래 이력 조회 (페이징)
     */
    Page<GemTransactionEntity> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);

    /**
     * 사용자의 총 젬 합계 조회
     */
    @Query("SELECT COALESCE(SUM(g.amount), 0) FROM GemTransactionEntity g WHERE g.user = :user")
    Integer getTotalGemsByUser(@Param("user") UserEntity user);

    /**
     * 사용자의 가장 최근 거래 조회 (잔액 확인용)
     */
    Optional<GemTransactionEntity> findTopByUserOrderByCreatedAtDesc(UserEntity user);
}
