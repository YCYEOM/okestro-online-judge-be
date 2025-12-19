package com.okestro.okestroonlinejudge.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.okestro.okestroonlinejudge.domain.SolutionEntity;
import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 정답 공유 목록 응답 DTO.
 */
@Schema(description = "정답 공유 목록 응답")
@Getter
@Builder
public class SolutionResponse {

    @Schema(description = "솔루션 ID")
    private Long id;

    @Schema(description = "문제 ID")
    private Long problemId;

    @Schema(description = "문제 제목")
    private String problemTitle;

    @Schema(description = "작성자 ID")
    private Long userId;

    @Schema(description = "작성자명")
    private String username;

    @Schema(description = "작성자 티어")
    private String userTier;

    @Schema(description = "언어")
    private String language;

    @Schema(description = "채점 결과")
    private SubmissionResult result;

    @Schema(description = "실행 시간 (ms)")
    private Integer executionTime;

    @Schema(description = "메모리 사용량 (KB)")
    private Integer memoryUsage;

    @Schema(description = "코드 길이")
    private Integer codeLength;

    @Schema(description = "생성 일시")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static SolutionResponse from(SolutionEntity solution) {
        return SolutionResponse.builder()
                .id(solution.getId())
                .problemId(solution.getProblem().getId())
                .problemTitle(solution.getProblem().getTitle())
                .userId(solution.getUser().getId())
                .username(solution.getUser().getUsername())
                .userTier(solution.getUser().getTierEntity().getGroupName())
                .language(solution.getLanguage())
                .result(solution.getSubmission().getResult())
                .executionTime(solution.getSubmission().getExecTimeMs())
                .memoryUsage(solution.getSubmission().getMemoryKb())
                .codeLength(solution.getCode().length())
                .createdAt(solution.getCreatedAt())
                .build();
    }
}
