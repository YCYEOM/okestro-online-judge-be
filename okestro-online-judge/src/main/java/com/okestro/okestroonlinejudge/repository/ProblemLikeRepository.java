package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.ProblemLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 문제 좋아요 Repository.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface ProblemLikeRepository extends JpaRepository<ProblemLikeEntity, Long> {

    /**
     * 문제 ID와 사용자 ID로 좋아요 정보를 조회한다.
     *
     * @param problemId 문제 ID
     * @param userId 사용자 ID
     * @return 좋아요 정보
     */
    @Query("SELECT l FROM ProblemLikeEntity l WHERE l.problem.id = :problemId AND l.user.id = :userId")
    Optional<ProblemLikeEntity> findByProblemIdAndUserId(@Param("problemId") Long problemId, @Param("userId") Long userId);

    /**
     * 문제 ID와 사용자 ID로 좋아요 여부를 확인한다.
     *
     * @param problemId 문제 ID
     * @param userId 사용자 ID
     * @return 좋아요 여부
     */
    @Query("SELECT COUNT(l) > 0 FROM ProblemLikeEntity l WHERE l.problem.id = :problemId AND l.user.id = :userId")
    boolean existsByProblemIdAndUserId(@Param("problemId") Long problemId, @Param("userId") Long userId);

    /**
     * 문제 ID로 좋아요 수를 조회한다.
     *
     * @param problemId 문제 ID
     * @return 좋아요 수
     */
    @Query("SELECT COUNT(l) FROM ProblemLikeEntity l WHERE l.problem.id = :problemId")
    Long countByProblemId(@Param("problemId") Long problemId);
}
