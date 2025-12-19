package com.okestro.okestroonlinejudge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포인트 정보 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointInfoResponse {

    /**
     * 총 포인트 (적립 - 사용)
     */
    private int totalPoints;

    /**
     * 적립된 총 포인트
     */
    private int earnedPoints;

    /**
     * 사용한 총 포인트
     */
    private int usedPoints;
}
