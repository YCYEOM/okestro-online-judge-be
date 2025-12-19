package com.okestro.okestroonlinejudge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 정답 공유 수정 요청 DTO.
 */
@Schema(description = "정답 공유 수정 요청")
@Getter
@NoArgsConstructor
public class UpdateSolutionRequest {

    @Schema(description = "설명", example = "설명을 수정합니다.")
    private String description;

    @Schema(description = "공개 여부", example = "false")
    private Boolean visibility;

    public UpdateSolutionRequest(String description, Boolean visibility) {
        this.description = description;
        this.visibility = visibility;
    }
}

