package com.okestro.okestroonlinejudge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 스트릭(잔디) 날짜별 데이터 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreakDateDto {

    /**
     * 날짜
     */
    private String date;

    /**
     * 해결한 문제 수
     */
    private Long count;

    /**
     * JPQL 결과를 위한 생성자 (타입 불일치 해결용).
     *
     * @param date 날짜 (String 또는 다른 타입)
     * @param count 해결 수
     */
    public StreakDateDto(Object date, Long count) {
        this.date = date != null ? date.toString() : null;
        this.count = count;
    }
}

