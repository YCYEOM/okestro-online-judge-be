package com.okestro.okestroonlinejudge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "댓글 작성/수정 요청")
public class CommentRequest {

    @Schema(description = "댓글 내용", example = "좋은 풀이네요!")
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

    @Builder
    public CommentRequest(String content) {
        this.content = content;
    }
}

