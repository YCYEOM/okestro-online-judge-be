package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 솔루션 좋아요 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "solution_like", uniqueConstraints = {
        @UniqueConstraint(name = "uk_solution_user", columnNames = {"solution_id", "user_id"})
})
public class SolutionLikeEntity extends BaseTimeEntity {

    /**
     * 좋아요 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 좋아요 대상 솔루션
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false)
    private SolutionEntity solution;

    /**
     * 좋아요 누른 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * SolutionLike 생성자.
     *
     * @param solution 솔루션
     * @param user 사용자
     */
    @Builder
    public SolutionLikeEntity(SolutionEntity solution, UserEntity user) {
        this.solution = solution;
        this.user = user;
    }
}

