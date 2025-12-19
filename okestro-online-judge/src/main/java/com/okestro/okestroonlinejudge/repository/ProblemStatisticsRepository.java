package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.ProblemStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 문제 통계 데이터 접근을 위한 리포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface ProblemStatisticsRepository extends JpaRepository<ProblemStatisticsEntity, Long> {
}


