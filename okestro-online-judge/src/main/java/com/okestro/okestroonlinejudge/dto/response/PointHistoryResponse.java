package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.PointEntity;
import com.okestro.okestroonlinejudge.domain.PointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 포인트 이력 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponse {

    /**
     * 포인트 이력 ID
     */
    private Long id;

    /**
     * 포인트 금액
     */
    private int amount;

    /**
     * 포인트 유형
     */
    private PointType type;

    /**
     * 포인트 사유
     */
    private String reason;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * Entity로부터 DTO 생성.
     */
    public static PointHistoryResponse from(PointEntity entity) {
        return PointHistoryResponse.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .type(entity.getType())
                .reason(entity.getReason())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
