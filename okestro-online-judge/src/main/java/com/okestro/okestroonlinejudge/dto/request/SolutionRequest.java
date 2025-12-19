package com.okestro.okestroonlinejudge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "정답 코드 공유 요청")
public class SolutionRequest {

    @Schema(description = "제출 ID (생성 시 필수, 수정 시 무시)", example = "1")
    private Long submissionId;

    @Schema(description = "설명", example = "이 코드는 DP를 사용하여 O(N)으로 풀었습니다.")
    private String description;

    @Schema(description = "공개 여부", example = "true")
    @NotNull(message = "공개 여부는 필수입니다.")
    private Boolean visibility;

    @Builder
    public SolutionRequest(Long submissionId, String description, Boolean visibility) {
        this.submissionId = submissionId;
        this.description = description;
        this.visibility = visibility;
    }
}

