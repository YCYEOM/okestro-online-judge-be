package com.okestro.okestroonlinejudge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 프로필 수정 요청 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "사용자 프로필 수정 요청")
public class UpdateProfileRequest {

    @Schema(description = "사용자명 (닉네임)", example = "coding_master")
    @Size(min = 2, max = 20, message = "사용자명은 2자 이상 20자 이하이어야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "사용자명은 영문, 숫자, 한글만 사용할 수 있습니다")
    private String userName;

    @Schema(description = "소속", example = "Okestro")
    @Size(max = 50, message = "소속은 50자를 초과할 수 없습니다")
    private String organization;

    @Schema(description = "자기소개", example = "안녕하세요, 알고리즘을 좋아하는 개발자입니다.")
    @Size(max = 200, message = "자기소개는 200자를 초과할 수 없습니다")
    private String bio;
}

