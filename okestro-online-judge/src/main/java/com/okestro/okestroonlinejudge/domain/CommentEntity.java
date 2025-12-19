package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
public class CommentEntity extends BaseTimeEntity {

    /**
     * 댓글 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 댓글이 달린 솔루션 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false)
    private SolutionEntity solution;

    /**
     * 댓글 작성자 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * 댓글 내용
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * Comment 생성자.
     *
     * @param solution 솔루션
     * @param user 작성자
     * @param content 내용
     */
    @Builder
    public CommentEntity(SolutionEntity solution, UserEntity user, String content) {
        this.solution = solution;
        this.user = user;
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
}


