package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 통계 정보를 나타내는 엔티티.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_statistics")
public class UserStatisticsEntity {

    /**
     * 사용자 ID (FK, PK)
     */
    @Id
    private Long id;

    /**
     * 대상 사용자
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    /**
     * 해결한 문제 수
     */
    @Column(name = "solved_count", nullable = false)
    private Long solvedCount = 0L;

    /**
     * 실패한 문제 수
     */
    @Column(name = "failed_count", nullable = false)
    private Long failedCount = 0L;

    /**
     * 랭킹 포인트
     */
    @Column(name = "ranking_point", nullable = false)
    private Long rankingPoint = 0L;

    /**
     * UserStatistics 생성자.
     *
     * @param userEntity 사용자
     */
    public UserStatisticsEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.solvedCount = 0L;
        this.failedCount = 0L;
        this.rankingPoint = 0L;
    }

    /**
     * 통계를 업데이트한다.
     *
     * @param solvedCount 해결 수
     * @param failedCount 실패 수
     * @param rankingPoint 랭킹 포인트
     */
    public void updateStatistics(Long solvedCount, Long failedCount, Long rankingPoint) {
        this.solvedCount = solvedCount;
        this.failedCount = failedCount;
        this.rankingPoint = rankingPoint;
    }
    
    /**
     * 문제를 해결했을 때 통계를 업데이트한다.
     *
     * @param problemScore 해결한 문제의 점수 (난이도에 따라 결정됨)
     */
    public void addSolvedProblem(int problemScore) {
        this.solvedCount++;
        this.rankingPoint += problemScore;
    }
    
    /**
     * 문제를 실패했을 때 통계를 업데이트한다.
     */
    public void addFailedProblem() {
        this.failedCount++;
    }
}


