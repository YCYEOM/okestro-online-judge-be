package com.okestro.okestroonlinejudge.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * 사용자 스트릭 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
public class UserStreakResponse {

    /**
     * 조회 연도
     */
    private Integer year;

    /**
     * 해당 연도 총 해결 문제 수
     */
    private Long totalSolved;

    /**
     * 최장 연속 해결 일수
     */
    private Integer maxStreak;

    /**
     * 현재 연속 해결 일수
     */
    private Integer currentStreak;

    /**
     * 날짜별 스트릭 데이터
     */
    private List<StreakDateDto> streaks;

    public static UserStreakResponse of(Integer year, Long totalSolved, Integer maxStreak, Integer currentStreak, List<StreakDateDto> streaks) {
        return UserStreakResponse.builder()
                .year(year)
                .totalSolved(totalSolved)
                .maxStreak(maxStreak)
                .currentStreak(currentStreak)
                .streaks(streaks)
                .build();
    }
}

