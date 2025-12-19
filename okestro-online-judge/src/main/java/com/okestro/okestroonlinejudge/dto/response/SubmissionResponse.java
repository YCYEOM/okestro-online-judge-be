package com.okestro.okestroonlinejudge.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.okestro.okestroonlinejudge.domain.SubmissionEntity;
import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 제출 정보 응답 DTO.
 *
 * FE profile.d.ts의 SubmissionHistory 타입과 일치:
 * {
 *   id: string;
 *   problemId: number;
 *   problemTitle: string;
 *   status: 'Accepted' | 'Wrong Answer' | 'Time Limit' | 'Runtime Error' | 'Compile Error';
 *   language: string;
 *   runtime?: string;
 *   memory?: string;
 *   submittedAt: string;
 * }
 *
 * @author Assistant
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {

    private String id;
    private Long problemId;
    private String problemTitle;
    private String status;
    private String language;
    private String runtime;
    private String memory;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;

    // 추가 필드 (BE에서 필요한 정보)
    private Long userId;
    private String username;
    private SubmissionResult resultEnum;
    private Integer totalTestCases;
    private Integer passedTestCases;
    private String errorMessage;

    public static SubmissionResponse from(SubmissionEntity submissionEntity) {
        String runtime = submissionEntity.getExecTimeMs() != null
                ? submissionEntity.getExecTimeMs() + "ms"
                : null;
        String memory = submissionEntity.getMemoryKb() != null
                ? submissionEntity.getMemoryKb() + "KB"
                : null;

        return SubmissionResponse.builder()
                .id(String.valueOf(submissionEntity.getId()))
                .problemId(submissionEntity.getProblemEntity().getId())
                .problemTitle(submissionEntity.getProblemEntity().getTitle())
                .status(submissionEntity.getResult().toDisplayName())
                .language(submissionEntity.getLanguage())
                .runtime(runtime)
                .memory(memory)
                .submittedAt(submissionEntity.getCreatedAt())
                .userId(submissionEntity.getUserEntity().getId())
                .username(submissionEntity.getUserEntity().getUsername())
                .resultEnum(submissionEntity.getResult())
                .build();
    }

    /**
     * 제출 결과와 테스트케이스 정보를 포함한 응답 생성
     */
    public static SubmissionResponse fromWithTestCases(SubmissionEntity submissionEntity, 
                                                        Integer totalTestCases, 
                                                        Integer passedTestCases) {
        SubmissionResponse response = from(submissionEntity);
        response.totalTestCases = totalTestCases;
        response.passedTestCases = passedTestCases;
        return response;
    }
}


