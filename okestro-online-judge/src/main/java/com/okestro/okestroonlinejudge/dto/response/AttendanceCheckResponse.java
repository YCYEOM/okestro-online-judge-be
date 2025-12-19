package com.okestro.okestroonlinejudge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 출석 체크 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckResponse {

    /**
     * 출석 체크 성공 여부
     */
    private boolean success;

    /**
     * 이미 출석했는지 여부
     */
    private boolean alreadyChecked;

    /**
     * 획득한 기본 포인트
     */
    private int pointsEarned;

    /**
     * 현재 연속 출석 일수
     */
    private int currentStreak;

    /**
     * 연속 출석 보너스 포인트
     */
    private int bonusPoints;

    /**
     * 응답 메시지
     */
    private String message;

    /**
     * 이미 출석 완료 응답 생성.
     */
    public static AttendanceCheckResponse alreadyChecked(int currentStreak) {
        return AttendanceCheckResponse.builder()
                .success(false)
                .alreadyChecked(true)
                .pointsEarned(0)
                .currentStreak(currentStreak)
                .bonusPoints(0)
                .message("오늘은 이미 출석체크를 완료했습니다.")
                .build();
    }

    /**
     * 출석 성공 응답 생성.
     */
    public static AttendanceCheckResponse success(int pointsEarned, int currentStreak, int bonusPoints) {
        String message = bonusPoints > 0
                ? String.format("출석 완료! %dP 획득 (연속 %d일 보너스 +%dP)", pointsEarned, currentStreak, bonusPoints)
                : String.format("출석 완료! %dP 획득 (연속 %d일)", pointsEarned, currentStreak);

        return AttendanceCheckResponse.builder()
                .success(true)
                .alreadyChecked(false)
                .pointsEarned(pointsEarned)
                .currentStreak(currentStreak)
                .bonusPoints(bonusPoints)
                .message(message)
                .build();
    }
}
