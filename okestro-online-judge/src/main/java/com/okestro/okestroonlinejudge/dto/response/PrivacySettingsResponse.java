package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로필 공개 설정 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacySettingsResponse {

    /**
     * 프로필 공개 여부
     */
    private Boolean profilePublic;

    /**
     * 통계 공개 여부
     */
    private Boolean statsPublic;

    /**
     * 풀이 기록 공개 여부
     */
    private Boolean solvedProblemsPublic;

    /**
     * 활동 기록 공개 여부
     */
    private Boolean activityPublic;

    public static PrivacySettingsResponse from(UserEntity entity) {
        return PrivacySettingsResponse.builder()
                .profilePublic(entity.getProfilePublic())
                .statsPublic(entity.getStatsPublic())
                .solvedProblemsPublic(entity.getSolvedProblemsPublic())
                .activityPublic(entity.getActivityPublic())
                .build();
    }
}
