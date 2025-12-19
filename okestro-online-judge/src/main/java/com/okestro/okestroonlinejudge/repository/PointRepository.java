package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.PointEntity;
import com.okestro.okestroonlinejudge.domain.PointType;
import com.okestro.okestroonlinejudge.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 포인트 데이터 접근을 위한 리포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface PointRepository extends JpaRepository<PointEntity, Long> {

    /**
     * 사용자의 포인트 이력 조회 (페이징).
     *
     * @param user     사용자
     * @param pageable 페이징 정보
     * @return 포인트 이력 페이지
     */
    Page<PointEntity> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);

    /**
     * 사용자의 총 포인트 합계 조회.
     *
     * @param user 사용자
     * @return 총 포인트 합계
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PointEntity p WHERE p.user = :user")
    Integer getTotalPointsByUser(@Param("user") UserEntity user);

    /**
     * 사용자의 특정 유형 포인트 합계 조회.
     *
     * @param user 사용자
     * @param type 포인트 유형
     * @return 포인트 합계
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PointEntity p WHERE p.user = :user AND p.type = :type")
    Integer getTotalPointsByUserAndType(@Param("user") UserEntity user, @Param("type") PointType type);
}
