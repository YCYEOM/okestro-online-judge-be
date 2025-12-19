package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.CommentEntity;
import com.okestro.okestroonlinejudge.domain.SolutionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 댓글(Comment) 데이터에 접근하기 위한 리포지토리 인터페이스.
 *
 * @author Assistant
 * @since 1.0
 */
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    /**
     * 특정 솔루션의 댓글 목록 조회.
     *
     * @param solution 솔루션
     * @param pageable 페이징 정보
     * @return 댓글 페이지
     */
    Page<CommentEntity> findBySolution(SolutionEntity solution, Pageable pageable);

    /**
     * 특정 솔루션의 댓글 수 조회.
     *
     * @param solution 솔루션
     * @return 댓글 수
     */
    long countBySolution(SolutionEntity solution);
}
