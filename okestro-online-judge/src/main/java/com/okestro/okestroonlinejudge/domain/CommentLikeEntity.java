package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 좋아요 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_like", uniqueConstraints = {
        @UniqueConstraint(name = "uk_comment_user", columnNames = {"comment_id", "user_id"})
})
public class CommentLikeEntity extends BaseTimeEntity {

    /**
     * 좋아요 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 좋아요 대상 댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity comment;

    /**
     * 좋아요 누른 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * CommentLike 생성자.
     *
     * @param comment 댓글
     * @param user 사용자
     */
    @Builder
    public CommentLikeEntity(CommentEntity comment, UserEntity user) {
        this.comment = comment;
        this.user = user;
    }
}

