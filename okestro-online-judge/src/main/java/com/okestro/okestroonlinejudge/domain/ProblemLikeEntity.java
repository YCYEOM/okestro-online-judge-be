package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문제 좋아요 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "problem_like", uniqueConstraints = {
        @UniqueConstraint(name = "uk_problem_user", columnNames = {"problem_id", "user_id"})
})
public class ProblemLikeEntity extends BaseTimeEntity {

    /**
     * 좋아요 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 좋아요 대상 문제
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProblemEntity problem;

    /**
     * 좋아요 누른 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * ProblemLike 생성자.
     *
     * @param problem 문제
     * @param user 사용자
     */
    @Builder
    public ProblemLikeEntity(ProblemEntity problem, UserEntity user) {
        this.problem = problem;
        this.user = user;
    }
}
