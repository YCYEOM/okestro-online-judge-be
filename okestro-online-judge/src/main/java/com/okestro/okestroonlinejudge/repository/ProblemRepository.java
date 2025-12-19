package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 문제 데이터 접근을 위한 리포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface ProblemRepository extends JpaRepository<ProblemEntity, Long>, JpaSpecificationExecutor<ProblemEntity> {

    /**
     * ID로 문제를 조회하며, creator와 tierEntity를 함께 fetch join으로 로드
     */
    @Query("SELECT p FROM ProblemEntity p " +
           "LEFT JOIN FETCH p.creator c " +
           "LEFT JOIN FETCH c.tierEntity " +
           "LEFT JOIN FETCH p.tierEntity " +
           "WHERE p.id = :id")
    Optional<ProblemEntity> findByIdWithCreator(@Param("id") Long id);
}


