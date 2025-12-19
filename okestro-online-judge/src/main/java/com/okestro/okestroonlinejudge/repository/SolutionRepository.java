package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.ProblemEntity;
import com.okestro.okestroonlinejudge.domain.SolutionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 정답 코드 공유(Solution) 데이터에 접근하기 위한 리포지토리 인터페이스.
 */
public interface SolutionRepository extends JpaRepository<SolutionEntity, Long> {

    Page<SolutionEntity> findByProblem(ProblemEntity problem, Pageable pageable);
    
    Page<SolutionEntity> findByProblemAndVisibilityTrue(ProblemEntity problem, Pageable pageable);

    Page<SolutionEntity> findByProblemAndLanguage(ProblemEntity problem, String language, Pageable pageable);

    Page<SolutionEntity> findByProblemAndLanguageAndVisibilityTrue(ProblemEntity problem, String language, Pageable pageable);

    @Query("SELECT s FROM SolutionEntity s " +
           "JOIN FETCH s.user u " +
           "JOIN FETCH s.problem p " +
           "WHERE s.id = :id")
    Optional<SolutionEntity> findByIdWithUserAndProblem(@Param("id") Long id);

    boolean existsBySubmissionId(Long submissionId);
}
