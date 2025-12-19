package com.okestro.okestroonlinejudge.repository;

import com.okestro.okestroonlinejudge.domain.CommentEntity;
import com.okestro.okestroonlinejudge.domain.CommentLikeEntity;
import com.okestro.okestroonlinejudge.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 댓글 좋아요(CommentLike) 데이터에 접근하기 위한 리포지토리 인터페이스.
 */
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {

    boolean existsByCommentAndUser(CommentEntity comment, UserEntity user);

    Optional<CommentLikeEntity> findByCommentAndUser(CommentEntity comment, UserEntity user);

    long countByComment(CommentEntity comment);
}

