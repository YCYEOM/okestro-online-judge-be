package com.okestro.okestroonlinejudge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 오늘 출석 여부 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayAttendanceResponse {

    /**
     * 오늘 출석 완료 여부
     */
    private boolean checked;

    /**
     * 현재 연속 출석 일수
     */
    private int currentStreak;

    /**
     * 오늘 획득한 포인트 (출석한 경우)
     */
    private int todayPoints;
}
