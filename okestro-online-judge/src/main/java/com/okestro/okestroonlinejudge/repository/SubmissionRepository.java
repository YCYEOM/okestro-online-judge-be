package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.SubmissionEntity;
import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.okestro.okestroonlinejudge.dto.response.StreakDateDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 제출 데이터 접근을 위한 리포지토리.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Long> {

    /**
     * 사용자 ID로 제출 목록 조회.
     *
     * @param userId 사용자 ID
     * @return 제출 목록
     */
    List<SubmissionEntity> findByUserEntity_Id(Long userId);

    /**
     * 사용자 ID로 제출 목록 조회 (페이징).
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 제출 페이지
     */
    Page<SubmissionEntity> findByUserEntity_Id(Long userId, Pageable pageable);

    /**
     * 문제 ID로 제출 목록 조회.
     *
     * @param problemId 문제 ID
     * @return 제출 목록
     */
    List<SubmissionEntity> findByProblemEntity_Id(Long problemId);

    /**
     * 특정 사용자가 특정 문제를 정답(ACCEPTED) 처리했는지 확인.
     *
     * @param problemId 문제 ID
     * @param userId 사용자 ID
     * @param result 제출 결과 (ACCEPTED)
     * @return 존재 여부
     */
    boolean existsByProblemEntity_IdAndUserEntity_IdAndResult(Long problemId, Long userId, SubmissionResult result);

    /**
     * 사용자의 기간 내 일별 문제 해결 수 조회.
     *
     * @param userId 사용자 ID
     * @param result 제출 결과 (ACCEPTED)
     * @param start 시작 일시
     * @param end 종료 일시
     * @return 날짜별 해결 수 목록
     */
    @Query("SELECT new com.okestro.okestroonlinejudge.dto.response.StreakDateDto(" +
            "FUNCTION('DATE_FORMAT', s.createdAt, '%Y-%m-%d'), COUNT(DISTINCT s.problemEntity.id)) " +
            "FROM SubmissionEntity s " +
            "WHERE s.userEntity.id = :userId " +
            "AND s.result = :result " +
            "AND s.createdAt BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE_FORMAT', s.createdAt, '%Y-%m-%d')")
    List<StreakDateDto> findDailySolvedCount(@Param("userId") Long userId,
                                             @Param("result") SubmissionResult result,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    /**
     * 사용자 ID, 문제 ID, 결과로 제출 이력 조회
     *
     * @param userId 사용자 ID
     * @param problemId 문제 ID
     * @param result 제출 결과
     * @return 제출 이력 목록
     */
    List<SubmissionEntity> findByUserEntityIdAndProblemEntityIdAndResult(
            @Param("userId") Long userId,
            @Param("problemId") Long problemId,
            @Param("result") SubmissionResult result
    );
}
