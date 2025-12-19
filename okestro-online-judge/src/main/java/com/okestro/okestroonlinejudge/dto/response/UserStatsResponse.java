package com.okestro.okestroonlinejudge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 통계 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {

    /**
     * 랭킹
     */
    private int rank;

    /**
     * 전체 사용자 수
     */
    private int totalUsers;

    /**
     * 해결한 문제 수
     */
    private int solvedCount;

    /**
     * 실패한 문제 수
     */
    private int failedCount;

    /**
     * 총 제출 수
     */
    private int totalSubmissions;

    /**
     * 정답률 (%)
     */
    private double acceptanceRate;

    /**
     * Easy 문제 해결 수
     */
    private int easyCount;

    /**
     * Medium 문제 해결 수
     */
    private int mediumCount;

    /**
     * Hard 문제 해결 수
     */
    private int hardCount;

    // === 게이미피케이션 데이터 ===

    /**
     * 티어 그룹 (BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, RUBY)
     */
    private String tier;

    /**
     * 티어 레벨 (1-5)
     */
    private int tierLevel;

    /**
     * 현재 XP (랭킹 포인트)
     */
    private int currentXP;

    /**
     * 다음 티어까지 필요한 XP
     */
    private int xpToNextTier;

    /**
     * 연속 풀이 일수
     */
    private int streak;

    /**
     * 오늘 스트릭 활성 여부
     */
    private boolean isStreakActive;

    // === 포인트 시스템 ===

    /**
     * 문제 풀이 포인트 (powerScore)
     */
    private int problemPoints;

    /**
     * 상점 포인트 (출석 등으로 획득한 젬)
     */
    private int shopPoints;
}
