package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문제 통계 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "problem_statistics")
public class ProblemStatisticsEntity {

    /**
     * 문제 ID (FK, PK)
     */
    @Id
    private Long id;

    /**
     * 대상 문제
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "problem_id")
    private ProblemEntity problemEntity;

    /**
     * 해결된 횟수
     */
    @Column(name = "solved_count", nullable = false)
    private Long solvedCount = 0L;

    /**
     * 시도 횟수
     */
    @Column(name = "try_count", nullable = false)
    private Long tryCount = 0L;

    /**
     * 정답률 (0.0 ~ 100.0)
     */
    @Column(name = "acceptance_rate", nullable = false)
    private Double acceptanceRate = 0.0;

    /**
     * 조회수
     */
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    /**
     * 좋아요 수
     */
    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    /**
     * 댓글 수
     */
    @Column(name = "comment_count", nullable = false)
    private Long commentCount = 0L;

    /**
     * ProblemStatistics 생성자.
     *
     * @param problemEntity 문제 엔티티
     */
    public ProblemStatisticsEntity(ProblemEntity problemEntity) {
        this.problemEntity = problemEntity;
    }

    /**
     * 통계 정보를 업데이트한다.
     *
     * @param solvedCount 해결 횟수
     * @param tryCount 시도 횟수
     */
    public void updateStatistics(Long solvedCount, Long tryCount) {
        this.solvedCount = solvedCount;
        this.tryCount = tryCount;
        if (tryCount > 0) {
            this.acceptanceRate = (double) solvedCount / tryCount * 100.0;
        } else {
            this.acceptanceRate = 0.0;
        }
    }

    /**
     * 조회수를 1 증가시킨다.
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 좋아요 수를 1 증가시킨다.
     */
    public void incrementLikeCount() {
        this.likeCount++;
    }

    /**
     * 좋아요 수를 1 감소시킨다.
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * 댓글 수를 1 증가시킨다.
     */
    public void incrementCommentCount() {
        this.commentCount++;
    }

    /**
     * 댓글 수를 1 감소시킨다.
     */
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
}


