package com.okestro.okestroonlinejudge.controller;

import com.okestro.okestroonlinejudge.dto.request.RunSampleRequest;
import com.okestro.okestroonlinejudge.dto.request.SubmitCodeRequest;
import com.okestro.okestroonlinejudge.dto.response.ApiResponse;
import com.okestro.okestroonlinejudge.dto.response.SampleTestResponse;
import com.okestro.okestroonlinejudge.dto.response.SubmissionResponse;
import com.okestro.okestroonlinejudge.service.SubmissionService;
import com.okestro.okestroonlinejudge.service.SubmissionService.TestCaseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 제출 관리 컨트롤러.
 *
 * @author Assistant
 * @since 1.0
 */
@Tag(name = "Submission", description = "제출 관련 API")
@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    /**
     * 코드를 제출하고 채점합니다.
     */
    @Operation(summary = "코드 제출", description = "코드를 제출하고 채점을 수행합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<SubmissionResponse>> submit(
            @Valid @RequestBody SubmitCodeRequest request
    ) {
        SubmissionResponse response = submissionService.submit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /**
     * 제출 상태 조회 (Polling용).
     * SSE 연결 실패 시 fallback으로 사용할 수 있습니다.
     */
    @Operation(summary = "제출 상태 조회", description = "제출 ID로 현재 채점 상태를 조회합니다.")
    @GetMapping("/{submissionId}/status")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionStatus(
            @Parameter(description = "제출 ID") @PathVariable Long submissionId
    ) {
        SubmissionResponse response = submissionService.getSubmission(submissionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 제출 ID로 제출 정보를 조회합니다.
     */
    @Operation(summary = "제출 조회", description = "제출 ID로 제출 정보를 조회합니다.")
    @GetMapping("/{submissionId}")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmission(
            @Parameter(description = "제출 ID") @PathVariable Long submissionId
    ) {
        SubmissionResponse response = submissionService.getSubmission(submissionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 사용자의 제출 목록을 조회합니다.
     */
    @Operation(summary = "사용자 제출 목록 조회", description = "특정 사용자의 제출 목록을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissionsByUser(
            @Parameter(description = "사용자 ID") @PathVariable Long userId
    ) {
        List<SubmissionResponse> response = submissionService.getSubmissionsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 문제의 제출 목록을 조회합니다.
     */
    @Operation(summary = "문제 제출 목록 조회", description = "특정 문제의 제출 목록을 조회합니다.")
    @GetMapping("/problem/{problemId}")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissionsByProblem(
            @Parameter(description = "문제 ID") @PathVariable Long problemId
    ) {
        List<SubmissionResponse> response = submissionService.getSubmissionsByProblem(problemId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 전체 제출 목록을 조회합니다 (최신순 정렬, 페이징 지원).
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 제출 목록
     */
    @Operation(summary = "전체 제출 목록 조회", description = "전체 제출 목록을 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getAllSubmissions(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size
    ) {
        List<SubmissionResponse> response = submissionService.getAllSubmissions(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 샘플 테스트케이스만 실행합니다 (제출 기록 없이 테스트용).
     *
     * @param request 샘플 실행 요청 DTO
     * @return 샘플 테스트케이스 실행 결과
     */
    @Operation(summary = "샘플 테스트케이스 실행", description = "샘플 테스트케이스만 실행하여 결과를 반환합니다. 제출 기록은 남지 않습니다.")
    @PostMapping("/run-sample")
    public ResponseEntity<ApiResponse<SampleTestResponse>> runSampleTestCases(
            @Valid @RequestBody RunSampleRequest request
    ) {
        try {
            List<TestCaseResult> results = submissionService.runSampleTestCases(
                    request.getProblemId(),
                    request.getSourceCode(),
                    request.getLanguage()
            );
            SampleTestResponse response = SampleTestResponse.from(results);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.internalError("샘플 실행 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
