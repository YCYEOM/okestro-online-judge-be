package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.TestCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 테스트케이스 데이터 접근을 위한 리포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Long> {

    /**
     * 문제 ID로 테스트케이스 목록 조회.
     *
     * @param problemId 문제 ID
     * @return 테스트케이스 목록
     */
    List<TestCaseEntity> findByProblemEntity_Id(Long problemId);
}


