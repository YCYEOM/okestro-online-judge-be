package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.ProblemCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 문제 댓글 Repository.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface ProblemCommentRepository extends JpaRepository<ProblemCommentEntity, Long> {

    /**
     * 문제 ID로 최상위 댓글 목록을 페이징하여 조회한다.
     *
     * @param problemId 문제 ID
     * @param pageable 페이징 정보
     * @return 댓글 목록 (Page)
     */
    @Query("SELECT c FROM ProblemCommentEntity c WHERE c.problem.id = :problemId AND c.parent IS NULL ORDER BY c.createdAt DESC")
    Page<ProblemCommentEntity> findByProblemIdAndParentIsNull(@Param("problemId") Long problemId, Pageable pageable);

    /**
     * 부모 댓글 ID로 대댓글 목록을 조회한다.
     *
     * @param parentId 부모 댓글 ID
     * @return 대댓글 목록
     */
    @Query("SELECT c FROM ProblemCommentEntity c WHERE c.parent.id = :parentId ORDER BY c.createdAt ASC")
    List<ProblemCommentEntity> findByParentId(@Param("parentId") Long parentId);

    /**
     * 문제 ID로 전체 댓글 수를 조회한다 (삭제되지 않은 것만).
     *
     * @param problemId 문제 ID
     * @return 댓글 수
     */
    @Query("SELECT COUNT(c) FROM ProblemCommentEntity c WHERE c.problem.id = :problemId AND c.isDeleted = false")
    Long countByProblemId(@Param("problemId") Long problemId);
}
