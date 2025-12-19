package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공개 프로필 응답 DTO (다른 사용자가 조회할 때 사용).
 * 젬(포인트)은 제외하고, 활동 관련 정보 포함.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicProfileResponse {

    // ==================== 기본 정보 ====================

    /**
     * 사용자 ID
     */
    private Long id;

    /**
     * 사용자명
     */
    private String userName;

    /**
     * 닉네임
     */
    private String nickname;

    /**
     * 프로필 이미지 URL
     */
    private String profileImage;

    /**
     * 소속
     */
    private String organization;

    /**
     * 자기소개
     */
    private String bio;

    /**
     * 가입일
     */
    private LocalDateTime createdAt;

    // ==================== 공개 설정 ====================

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

    // ==================== 활동 통계 (항상 공개) ====================

    /**
     * 출제한 문제 수 (커뮤니티 기여도)
     */
    private Integer createdProblemsCount;

    /**
     * 공유한 솔루션 수 (커뮤니티 기여도)
     */
    private Integer sharedSolutionsCount;

    // ==================== 통계 (공개 설정에 따라) ====================

    /**
     * 랭킹 (statsPublic이 true일 때만)
     */
    private Integer rank;

    /**
     * 맞힌 문제 수 (statsPublic이 true일 때만)
     */
    private Integer solvedCount;

    /**
     * 정답률 (statsPublic이 true일 때만)
     */
    private Double acceptanceRate;

    /**
     * 티어 (statsPublic이 true일 때만)
     */
    private String tier;

    /**
     * 티어 레벨 (statsPublic이 true일 때만)
     */
    private Integer tierLevel;

    // ==================== 활동 기록 (공개 설정에 따라) ====================

    /**
     * 연속 풀이 일수 (activityPublic이 true일 때만)
     */
    private Integer streak;

    /**
     * 오늘 활동 여부 (activityPublic이 true일 때만)
     */
    private Boolean isStreakActive;

    /**
     * Entity로부터 기본 정보만 포함한 DTO 생성.
     */
    public static PublicProfileResponse fromBasic(UserEntity entity) {
        String organization = entity.getOrganizationName();
        if (organization == null && entity.getOrganizationEntity() != null) {
            organization = entity.getOrganizationEntity().getName();
        }

        return PublicProfileResponse.builder()
                .id(entity.getId())
                .userName(entity.getUsername())
                .nickname(entity.getNickname())
                .profileImage(entity.getProfileImage())
                .organization(organization)
                .bio(entity.getBio())
                .createdAt(entity.getCreatedAt())
                .statsPublic(entity.getStatsPublic())
                .solvedProblemsPublic(entity.getSolvedProblemsPublic())
                .activityPublic(entity.getActivityPublic())
                // 커뮤니티 기여도는 항상 공개
                .createdProblemsCount(entity.getCreatedProblemsCount())
                .sharedSolutionsCount(entity.getSharedSolutionsCount())
                .build();
    }
}
