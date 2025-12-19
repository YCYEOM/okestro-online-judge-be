package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 프로필 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    /**
     * 사용자 ID
     */
    private Long id;

    /**
     * 이메일
     */
    private String email;

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

    /**
     * Entity로부터 DTO 생성.
     */
    public static UserProfileResponse from(UserEntity entity) {
        String organization = entity.getOrganizationName();
        if (organization == null && entity.getOrganizationEntity() != null) {
            organization = entity.getOrganizationEntity().getName();
        }

        return UserProfileResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .userName(entity.getUsername())
                .nickname(entity.getNickname())
                .profileImage(entity.getProfileImage())
                .organization(organization)
                .bio(entity.getBio())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
