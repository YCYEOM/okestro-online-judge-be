package com.okestro.okestroonlinejudge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 등급(티어) 정보를 나타내는 엔티티.
 * 문제 난이도 정보도 이 테이블을 사용하여 관리한다.
 *
 * @author Assistant
 * @since 1.0
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tier")
public class TierEntity {

    /**
     * 티어 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 티어 그룹 명 (예: Bronze, Silver, Gold)
     */
    @Column(name = "group_name", nullable = false)
    private String groupName;

    /**
     * 티어 레벨 (예: 1, 2, 3, 4, 5)
     */
    @Column(nullable = false)
    private Integer level;

    /**
     * 티어 점수 (내부 계산용 점수)
     */
    @Column(name = "power_score")
    private Integer powerScore;

    /**
     * 해당 티어 진입 최소 점수
     */
    @Column(name = "min_score")
    private Integer minScore;

    /**
     * 해당 티어 최대 점수
     */
    @Column(name = "max_score")
    private Integer maxScore;

    /**
     * 해당 난이도의 문제 해결 시 획득 점수
     */
    @Column(name = "problem_score")
    private Integer problemScore;

    /**
     * Tier 생성자.
     *
     * @param groupName 티어 그룹 명
     * @param level 티어 레벨
     * @param powerScore 티어 점수
     * @param minScore 최소 점수
     * @param maxScore 최대 점수
     * @param problemScore 문제 해결 점수
     */
    public TierEntity(String groupName, Integer level, Integer powerScore, Integer minScore, Integer maxScore, Integer problemScore) {
        this.groupName = groupName;
        this.level = level;
        this.powerScore = powerScore;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.problemScore = problemScore;
    }
}
