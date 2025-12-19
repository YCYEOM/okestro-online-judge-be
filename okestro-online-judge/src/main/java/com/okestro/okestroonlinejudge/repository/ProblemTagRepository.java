package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.ProblemTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 문제 태그 데이터 접근을 위한 리포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface ProblemTagRepository extends JpaRepository<ProblemTagEntity, Long> {

    /**
     * 태그 이름으로 조회.
     *
     * @param name 태그 이름
     * @return 태그 엔티티 Optional
     */
    Optional<ProblemTagEntity> findByName(String name);
}


