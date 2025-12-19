package com.okestro.okestroonlinejudge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 문제 댓글 작성/수정 요청 DTO.
 */
@Getter
@NoArgsConstructor
@Schema(description = "문제 댓글 작성/수정 요청")
public class ProblemCommentRequest {

    @Schema(description = "댓글 내용", example = "이 문제 정말 어렵네요!")
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

    @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "1")
    private Long parentId;

    @Builder
    public ProblemCommentRequest(String content, Long parentId) {
        this.content = content;
        this.parentId = parentId;
    }
}
