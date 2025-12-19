package com.okestro.okestroonlinejudge.dto.response;

import com.okestro.okestroonlinejudge.domain.SubmissionResult;
import com.okestro.okestroonlinejudge.service.SubmissionService.TestCaseResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 샘플 테스트케이스 실행 결과 응답 DTO.
 *
 * @author Assistant
 * @since 1.0
 */
@Schema(description = "샘플 테스트케이스 실행 결과")
@Getter
@Builder
@AllArgsConstructor
public class SampleTestResponse {

    @Schema(description = "전체 결과 (모두 통과하면 ACCEPTED)")
    private SubmissionResult overallStatus;

    @Schema(description = "총 테스트케이스 수")
    private int totalTestCases;

    @Schema(description = "통과한 테스트케이스 수")
    private int passedTestCases;

    @Schema(description = "컴파일 에러 메시지")
    private String compileError;

    @Schema(description = "각 테스트케이스별 실행 결과")
    private List<TestCaseResultDto> testResults;

    @Schema(description = "테스트케이스 실행 결과 (개별)")
    @Getter
    @Builder
    @AllArgsConstructor
    public static class TestCaseResultDto {
        
        @Schema(description = "테스트케이스 ID")
        private Long testCaseId;

        @Schema(description = "실행 결과 (ACCEPTED, WRONG_ANSWER 등)")
        private SubmissionResult status;

        @Schema(description = "실행 시간 (ms)")
        private Integer runtime;

        @Schema(description = "메모리 사용량 (KB)")
        private Integer memory;

        @Schema(description = "입력 데이터")
        private String input;

        @Schema(description = "예상 출력")
        private String expectedOutput;

        @Schema(description = "실제 출력")
        private String actualOutput;

        @Schema(description = "에러 메시지")
        private String errorMessage;

        @Schema(description = "숨김 여부 (false - 샘플이므로 모두 공개)")
        private boolean isHidden;
    }

    /**
     * TestCaseResult 리스트로부터 SampleTestResponse 생성
     */
    public static SampleTestResponse from(List<TestCaseResult> testCaseResults) {
        if (testCaseResults == null || testCaseResults.isEmpty()) {
            return SampleTestResponse.builder()
                    .overallStatus(SubmissionResult.RUNTIME_ERROR)
                    .totalTestCases(0)
                    .passedTestCases(0)
                    .compileError("테스트케이스가 없습니다")
                    .testResults(List.of())
                    .build();
        }

        int passedCount = (int) testCaseResults.stream()
                .filter(r -> r.getResult() == SubmissionResult.ACCEPTED)
                .count();

        SubmissionResult overallStatus = testCaseResults.stream()
                .map(TestCaseResult::getResult)
                .filter(result -> result != SubmissionResult.ACCEPTED)
                .findFirst()
                .orElse(SubmissionResult.ACCEPTED);

        List<TestCaseResultDto> dtoList = testCaseResults.stream()
                .map(tcr -> TestCaseResultDto.builder()
                        .testCaseId(tcr.getTestCaseId())
                        .status(tcr.getResult())
                        .runtime(tcr.getExecutionTime() != null ? (int) (tcr.getExecutionTime() * 1000) : null)
                        .memory(tcr.getMemoryUsage())
                        .input(tcr.getInput())
                        .expectedOutput(tcr.getExpectedOutput())
                        .actualOutput(tcr.getActualOutput())
                        .errorMessage(tcr.getErrorMessage())
                        .isHidden(false) // 샘플은 모두 공개
                        .build())
                .collect(Collectors.toList());

        return SampleTestResponse.builder()
                .overallStatus(overallStatus)
                .totalTestCases(testCaseResults.size())
                .passedTestCases(passedCount)
                .testResults(dtoList)
                .build();
    }
}

