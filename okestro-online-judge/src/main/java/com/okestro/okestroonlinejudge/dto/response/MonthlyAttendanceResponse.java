package com.okestro.okestroonlinejudge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 월간 출석 현황 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAttendanceResponse {

    /**
     * 조회 년도
     */
    private int year;

    /**
     * 조회 월
     */
    private int month;

    /**
     * 출석한 날짜 목록 (YYYY-MM-DD 형식)
     */
    private List<String> attendedDates;

    /**
     * 해당 월 총 출석 일수
     */
    private int totalDays;

    /**
     * 현재 연속 출석 일수
     */
    private int currentStreak;

    /**
     * 최장 연속 출석 일수
     */
    private int longestStreak;
}
