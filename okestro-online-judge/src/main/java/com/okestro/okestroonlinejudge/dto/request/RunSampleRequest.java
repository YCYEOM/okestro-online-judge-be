package com.okestro.okestroonlinejudge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 샘플 테스트케이스 실행 요청 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Schema(description = "샘플 테스트케이스 실행 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunSampleRequest {

    @Schema(description = "문제 ID", example = "1")
    @NotNull(message = "문제 ID는 필수입니다")
    private Long problemId;

    @Schema(description = "프로그래밍 언어", example = "Python")
    @NotBlank(message = "언어는 필수입니다")
    private String language;

    @Schema(description = "소스 코드")
    @NotBlank(message = "소스 코드는 필수입니다")
    private String sourceCode;
}

