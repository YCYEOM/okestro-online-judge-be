package com.okestro.okestroonlinejudge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로필 공개 설정 변경 요청 DTO.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePrivacyRequest {

    /**
     * 프로필 공개 여부
     */
    private Boolean profilePublic;

    /**
     * 통계 공개 여부 (정답률, 풀이 수 등)
     */
    private Boolean statsPublic;

    /**
     * 풀이 기록 공개 여부
     */
    private Boolean solvedProblemsPublic;

    /**
     * 활동 기록 공개 여부 (스트릭 등)
     */
    private Boolean activityPublic;
}
