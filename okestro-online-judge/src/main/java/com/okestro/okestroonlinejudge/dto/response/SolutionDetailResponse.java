package com.okestro.okestroonlinejudge.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.okestro.okestroonlinejudge.domain.SolutionEntity;
import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 정답 공유 상세 응답 DTO.
 */
@Schema(description = "정답 공유 상세 응답")
@Getter
@Builder
public class SolutionDetailResponse {

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

    @Schema(description = "소스 코드")
    private String code;

    @Schema(description = "설명")
    private String description;

    @Schema(description = "채점 결과")
    private SubmissionResult result;

    @Schema(description = "실행 시간 (ms)")
    private Integer executionTime;

    @Schema(description = "메모리 사용량 (KB)")
    private Integer memoryUsage;

    @Schema(description = "좋아요 수")
    private Long likeCount;

    @Schema(description = "댓글 수")
    private Long commentCount;

    @Schema(description = "현재 사용자 좋아요 여부")
    private boolean liked;

    @Schema(description = "생성 일시")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static SolutionDetailResponse of(SolutionEntity solution, long likeCount, long commentCount, boolean liked) {
        return SolutionDetailResponse.builder()
                .id(solution.getId())
                .problemId(solution.getProblem().getId())
                .problemTitle(solution.getProblem().getTitle())
                .userId(solution.getUser().getId())
                .username(solution.getUser().getUsername())
                .userTier(solution.getUser().getTierEntity().getGroupName())
                .language(solution.getLanguage())
                .code(solution.getCode())
                .description(solution.getDescription())
                .result(solution.getSubmission().getResult())
                .executionTime(solution.getSubmission().getExecTimeMs())
                .memoryUsage(solution.getSubmission().getMemoryKb())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .liked(liked)
                .createdAt(solution.getCreatedAt())
                .build();
    }
}

