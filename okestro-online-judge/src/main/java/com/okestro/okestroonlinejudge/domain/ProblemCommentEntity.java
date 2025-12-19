package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문제 댓글 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "problem_comment")
public class ProblemCommentEntity extends BaseTimeEntity {

    /**
     * 댓글 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 댓글이 달린 문제 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemEntity problem;

    /**
     * 댓글 작성자 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * 부모 댓글 (대댓글인 경우)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProblemCommentEntity parent;

    /**
     * 댓글 내용
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 삭제 여부 (소프트 삭제)
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    /**
     * ProblemComment 생성자.
     *
     * @param problem 문제
     * @param user 작성자
     * @param parent 부모 댓글 (대댓글인 경우)
     * @param content 내용
     */
    @Builder
    public ProblemCommentEntity(ProblemEntity problem, UserEntity user, ProblemCommentEntity parent, String content) {
        this.problem = problem;
        this.user = user;
        this.parent = parent;
        this.content = content;
    }

    /**
     * 댓글 내용을 수정한다.
     *
     * @param content 수정할 내용
     */
    public void update(String content) {
        this.content = content;
    }

    /**
     * 댓글을 소프트 삭제 처리한다.
     */
    public void delete() {
        this.isDeleted = true;
        this.content = "삭제된 댓글입니다.";
    }
}
