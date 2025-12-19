package com.okestro.okestroonlinejudge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 정답 공유 생성 요청 DTO.
 */
@Schema(description = "정답 공유 생성 요청")
@Getter
@NoArgsConstructor
public class CreateSolutionRequest {

    @Schema(description = "제출 ID", example = "123")
    @NotNull(message = "제출 ID는 필수입니다.")
    private Long submissionId;

    @Schema(description = "설명", example = "이 문제는 DP로 풀 수 있습니다.")
    private String description;

    @Schema(description = "공개 여부", example = "true")
    private Boolean visibility;

    public CreateSolutionRequest(Long submissionId, String description, Boolean visibility) {
        this.submissionId = submissionId;
        this.description = description;
        this.visibility = visibility != null ? visibility : true;
    }
}

