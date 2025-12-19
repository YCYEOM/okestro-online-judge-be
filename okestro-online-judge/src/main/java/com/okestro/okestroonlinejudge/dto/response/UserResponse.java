package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String userName;
    private String nickname;
    private String groupName;
    private String role;
    private String profileImage;

    public static UserResponse from(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getUsername())
                .nickname(user.getNickname())
                .groupName(user.getOrganizationEntity() != null ?
                        user.getOrganizationEntity().getName() : null)
                .role(user.getRole().name())
                .profileImage(user.getProfileImage())
                .build();
    }
}
